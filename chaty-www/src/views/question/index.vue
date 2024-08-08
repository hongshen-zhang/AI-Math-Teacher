<template>
    <div class="container">
        <el-form class="header-bar" :inline="true">
            <el-form-item label="题目：">
                <el-input v-model="filter.question" />
            </el-form-item>
            <el-form-item label="知识点：">
                <el-input v-model="filter.knowledge" />
            </el-form-item>

            <el-form-item>
                <el-button type="primary" @click="loadQuestions">查询</el-button>
                <el-button type="primary" @click="handleAdd">新增</el-button>
            </el-form-item>
        </el-form>
        <el-table v-loading="loading" :data="questions" style="width: 100%" :border="true" size="large" 
            class="main-table" empty-text="无数据">
            <template v-for="column in columns" :key="column.prop">
                <el-table-column v-bind="column">
                    <template v-if="column.prop === 'operations'" #default="scope">
                        <el-button type="primary" @click="handleEdit(scope.row)">编辑</el-button>
                        <el-button type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
                    </template>
                    <template v-else-if="column.type === 'textarea'" #default="scope">
                        <el-scrollbar max-height="200px">
                            <el-text style="white-space: pre-wrap; word-break: break-all;">
                                {{ scope.row[column.prop] }}
                            </el-text>
                        </el-scrollbar>
                    </template>
                </el-table-column>
            </template>
        </el-table>
        <form-dialog :isShow="isShowForm" :title="formTitle" :data="checkedData" @onClose="onFormClose"></form-dialog>
    </div>
</template>

<script>
import FormDialog from './form-dialog.vue'
export default {
    components: {
        FormDialog,
    },
    data() {
        return {
            columns: [
                {
                    prop: "question",
                    label: "题目",
                    type: 'textarea',
                },
                {
                    prop: "answer",
                    label: "答案",
                    type: 'textarea',
                },
                {
                    prop: 'knowledge',
                    label: '知识点',
                    type: 'textarea',
                },
                {
                    prop: 'operations',
                    label: '操作',
                }
            ],
            filter: {
                question: '',
                knowledge: '',
            },
            questions: [],
            loading: false,
            isShowForm: false,
            formTitle: '',
            checkedData: {},
        }
    },
    created() {
        this.loadQuestions()
    },
    methods: {
        loadQuestions() {
            this.loading = true
            let params = {}
            if (this.filter.question && this.filter.question !== '') {
                params.question = this.filter.question
            }
            if (this.filter.knowledge && this.filter.knowledge !== '') {
                params.knowledge = this.filter.knowledge
            }
            this.$axios.get("/api/questionLibrary/findAll", { params }).then(res => {
                this.questions = res.data
            }).finally(() => {
                this.loading = false
            })
        },
        handleAdd() {
            this.formTitle = "新增"
            this.isShowForm = true
        },
        onFormClose(submited) {
            this.isShowForm = false
            if (submited) {
                this.checkedData = {}
                this.loadQuestions()
            }
        },
        handleEdit(data) {
            this.formTitle = '编辑'
            this.checkedData = data
            this.isShowForm = true
        },
        handleDelete(id) {
            this.$confirm('确认删除？', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$axios.post(`/api/questionLibrary/delete?id=${id}`).then(res => {
                    this.$message.success('删除成功')
                    this.loadQuestions()
                })
            })
        }
    }
}
</script>

<style lang="scss" scoped>
.container {
    display: flex;
    flex-direction: column;
    height: 100%;

    .header-bar {
        padding: 10px;
    }

    .main-table {
        flex: 1;
    }
}
</style>