<template>
    <div class="input-wrapper">
        <v-md-preview class="view-md" v-if="!editable" :text="content" @click="updateEditStatus"></v-md-preview>
        <el-input ref="input" v-show="editable" v-bind="$attrs" @blur="updateEditStatus" @change="updateEditStatus" @input="(val) => content=val"></el-input>
    </div>
</template>

<script>
export default {
    props: {
    },
    data() {
        return {
            editable: false,
            content: ''
        }
    },
    methods: {
        updateEditStatus() {
            this.editable = !this.editable
            if (this.editable) {
                this.$refs.input.focus()
            }
        },
    }
}
</script>

<style lang="scss" scoped>
.input-wrapper {
    width: 100%;
}

.view-md {
    padding: 5px 11px;
    border-radius: var(--el-input-border-radius,var(--el-border-radius-base));
    border: 1px solid var(--el-border-color);

    :deep(.github-markdown-body) {
        font-size: var(--el-font-size-base);
        line-height: 1.5;
        color: var(--el-input-text-color,var(--el-text-color));
        padding: 0;
        min-height: 20px;

        p,
        pre {
            margin-bottom: 0;
        }
    }
}
</style>