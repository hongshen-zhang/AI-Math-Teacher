<template>
    <div class="container">
        <el-form class="header-bar" :inline="true" label-width="100px">
            <el-form-item label="名称：">
                <el-input v-model="filter.name" />
            </el-form-item>
            <el-form-item label="题目：">
                <el-input v-model="filter.question" />
            </el-form-item>
            <el-form-item label="答案：">
                <el-input v-model="filter.answer" />
            </el-form-item>
            <el-form-item label="是否正确：">
                <el-select style="width: 196.4px"  v-model="filter.isTrue" placeholder=" " clearable>
                    <el-option value="0" label="错误"></el-option>
                    <el-option value="1" label="正确"></el-option>
                </el-select>
            </el-form-item>

            <el-form-item>
                <el-button type="primary" @click="loadReviews">查询</el-button>
                <el-button type="primary" @click="handleAdd">新增</el-button>
            </el-form-item>
        </el-form>
        <el-table v-loading="loading" :data="reviews" style="width: 100%" :border="true" size="large" class="main-table"
            empty-text="无数据">
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
                    <template v-else-if="column.type === 'dic'" #default="scope">
                        <el-tag>{{ column.dic[scope.row[column.prop]] }}</el-tag>
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
                    prop: "name",
                    label: "名称",
                },
                {
                    prop: "question",
                    label: "题目",
                    type: 'textarea',
                },
                {
                    prop: "correctAnswer",
                    label: "正确答案",
                    type: 'textarea',
                },
                {
                    prop: 'knowledge',
                    label: '知识点',
                    type: 'textarea',
                },
                {
                    prop: "answer",
                    label: "答案",
                    type: 'textarea',
                },
                {
                    prop: "isTrue",
                    label: "是否正确",
                    type: 'dic',
                    dic: {
                        0: '错误',
                        1: '正确', 
                    },
                    align: 'center',
                    width: '90px'
                },
                {
                    prop: "errText",
                    label: "错误",
                    type: 'textarea',
                },
                {
                    prop: "comment",
                    label: "评价",
                    type: 'textarea',
                },
                {
                    prop: 'operations',
                    label: '操作',
                    width: '170px',
                }
            ],
            filter: {
                question: '',
                answer: '',
                isTrue: null,
                name: '',
            },
            reviews: [],
            loading: false,
            isShowForm: false,
            formTitle: '',
            checkedData: {},
        }
    },
    created() {
        this.loadReviews()
    },
    methods: {
        loadReviews() {
            this.loading = true
            let params = {
                name: this.filter.name,
                question: this.filter.question,
                answer: this.filter.answer,
                isTrue: this.filter.isTrue,
            }
            // TODO
            this.$axios.get("/api/review/list", { params }).then(res => {
                this.reviews = res.data
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
                this.loadReviews()
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
                this.$axios.post(`/api/review/delete?id=${id}`).then(res => {
                    this.$message.success('删除成功')
                    this.loadReviews()
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