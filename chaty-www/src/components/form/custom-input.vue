<template>
    <el-input v-bind="$attrs" class="custom-input_wrapper" :disabled="disabled"
        :input-style="inputStyle">
        <slot></slot>
    </el-input>
</template>

<script>
export default {
    props: {
        color: {
            type: String,
            default: 'none'
        },
        disabled: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            inputStyle: {}
        }
    },
    watch: {
        disabled: {
            handler(val) {
                if (val) {
                    this.inputStyle = Object.assign(this.inputStyle, {
                        userSelect: 'none',
                        color: 'transparent',
                    })
                } else {
                    this.inputStyle = Object.assign(this.inputStyle, {
                        userSelect: 'contain',
                        color: `var(--el-color-${this.color})`,
                    })
                }
            },
            immediate: true
        },
    }
}
</script>

<style lang="scss" scoped>
.custom-input_wrapper {
    padding: 0;

    :deep(.el-textarea__inner),
    :deep(.el-input__wrapper) {
        box-shadow: none;
        resize: none;
        font-size: var(--el-text-font-size);
        background-color: var(--el-bg-color-page);
        border-radius: 10px;
        padding: 10px;
    }

    :deep(.el-textarea__inner:focus),
    :deep(.el-input__wrapper.is-focus) {
        box-shadow: 0 0 0 1px var(--el-input-focus-border-color) inset;
    }

}
</style>