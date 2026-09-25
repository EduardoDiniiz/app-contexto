#!/usr/bin/env node
// Salva no banco uma solução gerada no Claude Code, a partir de uma pasta com este formato:
//
//   <pasta>/solucao.json   { projectId, title, request, model?, contextExtensions?, contextQuery?,
//                            files: [{ path, action, language?, description? }] }
//   <pasta>/resumo.md      explicação da solução em Markdown
//   <pasta>/arquivos/<path> conteúdo completo de cada arquivo CREATE/MODIFY
//
// Uso:  node tools/salvar-solucao.mjs <pasta>             cria uma nova solução
//       node tools/salvar-solucao.mjs <pasta> --id <id>   substitui a solução existente
// API:  variável CONTEXTO_API (padrão http://127.0.0.1:8080)

import { readFile } from 'node:fs/promises';
import { join } from 'node:path';

const API = (process.env.CONTEXTO_API || 'http://127.0.0.1:8080').replace(/\/$/, '');

function parseArgs(argv) {
  const [dir, ...rest] = argv;
  if (!dir) fail('Informe a pasta da solução. Uso: node tools/salvar-solucao.mjs <pasta> [--id <id>]');
  const idIndex = rest.indexOf('--id');
  return { dir, id: idIndex >= 0 ? rest[idIndex + 1] : null };
}

function fail(message) {
  console.error(`ERRO: ${message}`);
  process.exit(1);
}

async function readText(path, description) {
  try {
    return await readFile(path, 'utf8');
  } catch {
    fail(`${description} não encontrado: ${path}`);
  }
}

async function buildPayload(dir) {
  const manifest = JSON.parse(await readText(join(dir, 'solucao.json'), 'Manifesto'));
  const summary = await readText(join(dir, 'resumo.md'), 'Resumo');
  const files = [];
  for (const file of manifest.files || []) {
    const content = file.action === 'DELETE'
      ? ''
      : await readText(join(dir, 'arquivos', file.path), `Conteúdo de ${file.path}`);
    files.push({ ...file, content });
  }
  return { ...manifest, summary, files };
}

async function send(payload, id) {
  const response = await fetch(`${API}/api/v1/solutions${id ? `/${id}` : ''}`, {
    method: id ? 'PUT' : 'POST',
    headers: { 'Content-Type': 'application/json; charset=utf-8' },
    body: JSON.stringify(payload),
  });
  const body = await response.json().catch(() => ({}));
  if (!response.ok) {
    const details = body.errors ? ` ${JSON.stringify(body.errors)}` : '';
    fail(`HTTP ${response.status}: ${body.message || 'erro desconhecido'}${details}`);
  }
  return body;
}

const { dir, id } = parseArgs(process.argv.slice(2));
const saved = await send(await buildPayload(dir), id).catch(err =>
  fail(`Não foi possível falar com a aplicação em ${API} (${err.cause?.code || err.message}). Ela está rodando?`));
console.log(`Solução ${id ? 'atualizada' : 'salva'}: #${saved.id} "${saved.title}" (${saved.files.length} arquivo(s))`);
console.log(`${API.replace('127.0.0.1', 'localhost')}/solucoes.html#${saved.id}`);
