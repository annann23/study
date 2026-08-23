import { JSDOM } from 'jsdom'
import fs from 'node:fs'

const dom = new JSDOM('<!doctype html><html><body></body></html>')
global.window = dom.window
global.document = dom.window.document
Object.defineProperty(global, 'navigator', { value: dom.window.navigator, configurable: true })
global.DOMParser = dom.window.DOMParser
global.Node = dom.window.Node
global.Element = dom.window.Element
global.HTMLElement = dom.window.HTMLElement
global.getComputedStyle = dom.window.getComputedStyle

const { Editor } = await import('@tiptap/core')
const { default: StarterKit } = await import('@tiptap/starter-kit')
const { default: Image } = await import('@tiptap/extension-image')
const { default: Placeholder } = await import('@tiptap/extension-placeholder')

const raw = JSON.parse(fs.readFileSync(process.argv[2], 'utf-8'))
const content = raw.content
console.log('input length:', content.length, 'has <img:', content.includes('<img'))

const editor = new Editor({
  extensions: [
    StarterKit,
    Image,
    Placeholder.configure({ placeholder: 'placeholder' }),
  ],
  content,
})

const out = editor.getHTML()
console.log('output length:', out.length, 'has <img:', out.includes('<img'))
if (!out.includes('<img')) {
  console.log('--- output snippet ---')
  console.log(out.slice(0, 500))
}
