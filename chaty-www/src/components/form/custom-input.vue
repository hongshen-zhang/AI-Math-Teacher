<template>
    <div class="custom-input_wrapper">
        <el-input v-bind="$attrs" class="custom-input" :disabled="disabled" :input-style="inputStyle">
            <slot></slot>
        </el-input>
        <div class="fixed-br">
            <slot name="fixed-br"></slot>
        </div>
        <div class="fixed-poper">
            <slot name="fixed-poper"></slot>
        </div>
    </div>
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
            inputStyle: {
            }
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
    position: relative;

    .custom-input {
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

    .fixed-br {
        position: absolute;
        bottom: 0;
        right: 0;
    }

    .fixed-poper {
        position: absolute;
        top: -20px;
        right: 0;
    }

}
</style>