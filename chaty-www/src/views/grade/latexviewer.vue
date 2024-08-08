<template>
    <div v-html="renderedHtml">
    </div>
</template>

<script>
import { parse, HtmlGenerator } from 'latex.js'
import { headerTex, reviewTex, testTex } from '../../utils/latexTemplate'
export default {
    props: {
        data: {
            type: Object,
            default: () => {
                return {
                    answer: 'Lorem ipsum dolor sit amet',
                    review: 'Lorem ipsum dolor sit amet'
                }
            }
        }
    },
    data() {
        return {
            renderedHtml: ''
        }
    },
    computed: {
        renderedHtml() {
            // let latex = headerTex();
            // latex += reviewTex(this.data);

            let latex = testTex();

            let generator = new HtmlGenerator({ hyphenate: false })
            let doc = parse(latex, { generator: generator }).htmlDocument()
            console.log(doc.documentElement.outerHTML)
            return doc.documentElement.outerHTML;
        }
    }
}
</script>

<style lang="scss" scoped></style>