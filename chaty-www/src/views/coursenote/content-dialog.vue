<template>
    <el-dialog v-model="isShow" :title="title" width="30%" :before-close="() => beforeClose()">
        <el-form :model="form" label-width="120px">
            <el-form-item prop="type">
                <el-radio-group class="type-radio" :model-value="typeName" @change="onTypeChange">
                    <el-radio-button label="知识点" />
                    <el-radio-button label="题目" />
                </el-radio-group>
            </el-form-item>
            <el-form-item v-show="form.type === 1" label="题目：" prop="question" :style="{ width: formWidth }">
                <el-input v-model="form.content" :autosize="{ minRows: 5, maxRows: 7 }" type="textarea" />
            </el-form-item>
            <el-form-item v-show="form.type === 1" label="答案：" prop="amswer" :style="{ width: formWidth }">
                <el-input v-model="form.content" :autosize="{ minRows: 5, maxRows: 7 }" type="textarea" />
            </el-form-item>
            <el-form-item label="知识点：" prop="content" :style="{ width: formWidth }">
                <el-input v-model="form.content" :autosize="{ minRows: 5, maxRows: 7 }" type="textarea" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary">确认</el-button>
                <el-button>取消</el-button>
            </el-form-item>
        </el-form>
    </el-dialog>
</template>

<script>
export default {
    data() {
        return {
            isShow: false,
            title: '',
            form: {
                type: 0, // 0-知识点 1-题目
            },
            formWidth: '500px',
        }
    },
    computed: {
        typeName() {
            return this.form.type === 0 ? '知识点' : '题目'
        }
    },
    methods: {
        show(data) {
            if (data) {
                this.title = '编辑'
            } else {
                this.title = '新增'
            }
            this.isShow = true
        },
        beforeClose() {
            this.isShow = false
        },
        onTypeChange(typeName) {
            if (typeName === '题目') {
                this.form.type = 1
                this.resetForm({ type: 1 })
            }
            if (typeName === '知识点') {
                this.form.type = 0
                this.resetForm({ type: 0 })
            }
        },
        resetForm(ctx) {
            this.form = Object.assign({
                type: 0,
                content: '',
                question: '',
                answer: '',
            }, ctx)
        }
    }
}
</script>

<style lang="scss" scoped></style>