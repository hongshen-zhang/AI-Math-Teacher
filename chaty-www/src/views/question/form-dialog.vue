<template>
    <el-dialog :model-value="isShow" :title="title" width="600" :before-close="() => beforeClose()">
        <el-form :model="form" label-width="120px">
            <el-form-item label="题目：" prop="question" :style="{ width }">
                <el-input type="textarea" rows="5" v-model="form.question" />
            </el-form-item>
            <el-form-item label="答案：" prop="answer" :style="{ width }">
                <el-input type="textarea" rows="5" v-model="form.answer" />
            </el-form-item>
            <el-form-item label="知识点：" prop="knowledge" :style="{ width }">
                <el-input type="textarea" rows="5" v-model="form.knowledge" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="onsubmit">提交</el-button>
                <el-button type="primary" @click="beforeClose">取消</el-button>
            </el-form-item>
        </el-form>
    </el-dialog>
</template>

<script>
export default {
    props: {
        isShow: {
            type: Boolean,
            default: false,
        },
        title: {
            type: String,
            default: '',
        },
        data: {
            type: Object,
            default: () => { },
        },
    },
    data() {
        return {
            form: {
                question: '',
                answer: '',
                knowledge: '',
            },
            width: '500px',
        }
    },
    watch: {
        isShow(val) {
            if (val) {
                if (this.data) {
                    this.id = this.data.id
                    this.form = Object.assign(this.form, this.data)
                }
            }
        }
    },
    methods: {
        onsubmit() {
            if (this.id) {
                this.$axios.post(`/api/questionLibrary/update`, this.form).then(res => {
                    this.$message.success('修改成功')
                    this.beforeClose(true)
                })
            } else {
                this.$axios.post('/api/questionLibrary/add', this.form).then(res => {
                    this.$message.success('更新成功')
                    this.beforeClose(true)
                })
            }
        },
        beforeClose(submited) {
            this.form = {
                question: '',
                answer: '',
                knowledge: '',
            }
            this.$emit('onClose', submited)
        }
    }
}
</script>

<style lang="scss" scoped></style>