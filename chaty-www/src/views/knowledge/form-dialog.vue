<template>
    <el-dialog :model-value="isShow" :title="title" width="600" :before-close="() => beforeClose()">
        <el-form :model="form" label-width="120px">
            <el-form-item label="名称：" prop="name" :style="{ width }">
                <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="知识点：" prop="content" :style="{ width }">
                <el-input type="textarea" rows="5" v-model="form.content" />
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
    data() {
        return {
            form: {},
            width: '500px',
            isShow: false,
            title: '',
            id: ''
        }
    },
    methods: {
        show(data) {
            this.form = {
                name: '',
                content: '',
            }
            this.id = ''
            if (data) {
                this.form = Object.assign(this.form, data)
                this.id = data.id
                this.title = '编辑'
            } else {
                this.title = '新增'
            }
            this.isShow = true
        },
        onsubmit() {
            if (this.id) {
                this.$axios.post("/api/knowledge/update", this.form).then(res => {
                    this.$message.success("更新成功")
                    this.beforeClose();
                })
            } else {
                this.$axios.post("/api/knowledge/add", this.form).then(res => {
                    this.$message.success("新增成功")
                    this.beforeClose();
                })
            }
        },
        beforeClose() {
            this.isShow = false
            this.$emit('onClose')
        }
    }
}
</script>

<style lang="scss" scoped></style>