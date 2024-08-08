<template>
    <el-dialog :model-value="isShow" :title="title" width="600" :before-close="() => beforeClose()">
        <el-scrollbar max-height="600px">
            <el-form :model="form" label-width="120px">
                <el-form-item label="名称：" prop="name" :style="{ width }">
                    <el-input :autosize="inputAutoSize" v-model="form.name" />
                </el-form-item>
                <el-form-item label="题目：" prop="question" :style="{ width }">
                    <el-input type="textarea" :autosize="inputAutoSize" v-model="form.question" />
                </el-form-item>
                <el-form-item label="正确答案：" prop="correctAnswer" :style="{ width }">
                    <el-input type="textarea" :autosize="inputAutoSize" v-model="form.correctAnswer" />
                </el-form-item>
                <el-form-item label="知识点：" prop="knowledge" :style="{ width }">
                    <el-input type="textarea" :autosize="inputAutoSize" v-model="form.knowledge" />
                </el-form-item>
                <el-form-item label="答案：" prop="answer" :style="{ width }">
                    <el-input type="textarea" :autosize="inputAutoSize" v-model="form.answer" />
                </el-form-item>
                <el-form-item label="是否正确：" prop="isTrue" :style="{ width }">
                    <el-select v-model="form.isTrue" :style="{ width }"  placeholder=" " clearable>
                        <el-option label="正确" :value="1"></el-option>
                        <el-option label="错误" :value="0"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="错误：" prop="errText" :style="{ width }">
                    <el-input type="textarea" :autosize="inputAutoSize" v-model="form.errText" />
                </el-form-item>
                <el-form-item label="评价：" prop="comment" :style="{ width }">
                    <el-input type="textarea" :autosize="inputAutoSize" v-model="form.comment" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="onsubmit">提交</el-button>
                    <el-button type="primary" @click="beforeClose">取消</el-button>
                </el-form-item>
            </el-form>
        </el-scrollbar>
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
                name: '',
                question: '',
                correctAnswer: '',
                knowledge: '',
                answer: '',
                isTrue: null,
                errText: '',
                comment: '',
            },
            width: '500px',
            inputAutoSize: {
                minRows: 2,
                maxRows: 5,
            }
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
                this.$axios.post(`/api/review/update`, this.form).then(res => {
                    this.$message.success('修改成功')
                    this.beforeClose(true)
                })
            } else {
                this.$axios.post('/api/review/add', this.form).then(res => {
                    this.$message.success('更新成功')
                    this.beforeClose(true)
                })
            }
        },
        beforeClose(submited) {
            this.form = {
                name: '',
                question: '',
                correctAnswer: '',
                knowledge: '',
                answer: '',
                isTrue: null,
                errText: '',
                comment: '',
            }
            this.$emit('onClose', submited)
        }
    }
}
</script>

<style lang="scss" scoped></style>