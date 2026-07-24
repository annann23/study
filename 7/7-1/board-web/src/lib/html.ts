import DOMPurify from 'dompurify'

// allow data: URIs (base64-embedded images) in addition to DOMPurify's default schemes
const ALLOWED_URI_REGEXP = /^(?:(?:(?:f|ht)tps?|mailto|tel|callto|sms|cid|xmpp|data):|[^a-z]|[a-z+.-]+(?:[^a-z+.-:]|$))/i

export function sanitizeHtml(html: string): string {
  return DOMPurify.sanitize(html, { ALLOWED_URI_REGEXP })
}

export function stripHtml(html: string): string {
  const div = document.createElement('div')
  div.innerHTML = html
  return div.textContent ?? ''
}

export function firstImageSrc(html: string): string | null {
  const div = document.createElement('div')
  div.innerHTML = html
  return div.querySelector('img')?.getAttribute('src') ?? null
}
