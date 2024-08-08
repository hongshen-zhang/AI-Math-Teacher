<template>
    <div class="container-wrapper">
        <el-card class="left-bar">
            <template #header>
                <div class="card-header">
                    <span>配置</span>
                    <el-radio-group class="type-radio" :model-value="typeName" @change="onTypeChange">
                        <el-radio-button label="知识库" />
                        <el-radio-button label="大模型" />
                    </el-radio-group>
                </div>
            </template>
            <div class="library-container" v-if="type === 0">
                <!-- 知识库 -->
                <el-input v-model="library.search" placeholder="请输入题目" class="search-bar" size="large" suffix-icon="search"
                    @change="onSearchChange">
                    <template #prepend>
                        <el-select size="large" @change="onSearchChange" v-model="searchType" style="width: 110px">
                            <el-option label="全部" value="" />
                            <el-option label="题库" value="0" />
                            <el-option label="知识点" value="1" />
                            <el-option label="作业批改" value="2" />
                        </el-select>
                    </template>
                </el-input>
                <el-scrollbar>
                    <div class="content-list" v-loading="libraryLoading">
                        <transition-group name=".el-zoom-in-top">
                            <el-card v-for="library in librarys" :key="library.id" class="content-list-item">
                                <template #header>
                                    <div class="box-card-header">
                                        <span>{{ libraryTypeName(library.type) }}</span>
                                        <el-button type="primary" style="width: 70px"
                                            @click="addToNotes(library)">选择</el-button>
                                    </div>
                                </template>
                                <el-scrollbar max-height="200px" style="user-select: text;">
                                    <el-text v-if="library.type === 0">
                                        <el-text class="text-content" size="large" tag="p" type="primary">题目：</el-text>
                                        <el-text class="text-content" tag="p">{{ library.question }}</el-text>
                                        <el-text class="text-content" size="large" tag="p" type="primary">答案：</el-text>
                                        <el-text class="text-content" tag="p">{{ library.answer }}</el-text>
                                        <template v-if="library.knowledge">
                                            <el-text class="text-content" size="large" tag="p" type="primary">知识点：</el-text>
                                            <el-text class="text-content" tag="p">{{ library.knowledge }}</el-text>
                                        </template>
                                    </el-text>
                                    <el-text v-else-if="library.type === 1">
                                        <el-text class="text-content" size="large" tag="p" type="primary">名称：</el-text>
                                        <el-text class="text-content" tag="p">{{ library.knowledgeName }}</el-text>
                                        <template v-if="library.knowledge">
                                            <el-text class="text-content" size="large" tag="p" type="primary">知识点：</el-text>
                                            <el-text class="text-content" tag="p">{{ library.knowledge }}</el-text>
                                        </template>
                                    </el-text>
                                    <el-text v-else-if="library.type === 2">
                                        <el-text class="text-content" size="large" tag="p" type="primary">名称：</el-text>
                                        <el-text class="text-content" tag="p">{{ library.question }}</el-text>
                                        <el-text class="text-content" size="large" tag="p" type="primary">题目：</el-text>
                                        <el-text class="text-content" tag="p">{{ library.question }}</el-text>
                                        <el-text class="text-content" size="large" tag="p" type="primary">正确答案：</el-text>
                                        <el-text class="text-content" tag="p">{{ library.correctAnswer }}</el-text>
                                        <template v-if="library.knowledge">
                                            <el-text class="text-content" size="large" tag="p" type="primary">知识点：</el-text>
                                            <el-text class="text-content" tag="p">{{ library.knowledge }}</el-text>
                                        </template>
                                        <el-text class="text-content" size="large" tag="p" type="primary">学生答案：</el-text>
                                        <el-text class="text-content" tag="p">{{ library.answer }}</el-text>
                                        <el-text class="text-content" size="large" tag="p" type="primary">是否正确：</el-text>
                                        <el-text class="text-content" :type="library.isTrue ? 'success' : 'danger'"
                                            tag="p">{{ library.isTrue ? '正确' : '错误' }}</el-text>
                                        <template v-if="library.errText">
                                            <el-text class="text-content" size="large" tag="p" type="primary">错误：</el-text>
                                            <el-text class="text-content" :type="library.isTrue ? 'success' : 'danger'"
                                                tag="p">{{ library.errText }}</el-text>
                                        </template>
                                        <template v-if="library.comment">
                                            <el-text class="text-content" size="large" tag="p" type="primary">评价：</el-text>
                                            <el-text class="text-content" tag="p">{{ library.comment }}</el-text>
                                        </template>
                                    </el-text>
                                </el-scrollbar>
                            </el-card>
                        </transition-group>
                    </div>
                </el-scrollbar>
            </div>
            <div class="model-container" v-show="type === 1">
                <el-scrollbar>
                    <el-form ref="modelForm" class="model-form" :model="model" label-position="top" :rules="modelRules">
                        <el-form-item label="支持模型列表：" prop="aimodel">
                            <el-select v-model="model.aimodel" style="width: 100%;">
                                <el-option label="gpt-4" value="gpt-4" />
                                <el-option label="gpt-3.5-turbo" value="gpt-3.5-turbo" />
                                <el-option label="ERNIE-Bot" value="ERNIE-Bot" />
                                <!-- <el-option label="ERNIE-Bot-turbo" value="ERNIE-Bot-turbo" />
                            <el-option label="CPM hackthon" value="CPM hackthon" />
                            <el-option label="BLOOMZ-7B" value="BLOOMZ-7B" /> -->
                            </el-select>
                        </el-form-item>
                        <el-form-item label="提示预览：" prop="promot">
                            <el-input v-model="model.promot" :autosize="{ minRows: 5, maxRows: 7 }" type="textarea" />
                        </el-form-item>
                        <el-form-item label="题目：" prop="question">
                            <el-input v-model="model.question" :autosize="{ minRows: 5, maxRows: 7 }" type="textarea"
                                placeholder="请输入题目">
                            </el-input>
                        </el-form-item>
                        <el-form-item label="输出结果：" prop="result">
                            <div class="result-card" v-loading="modelLoading">
                                <div v-show="model.status === 1">
                                    <el-text class="text-content" size="large" tag="p" type="primary">答案：</el-text>
                                    <custom-input v-model="model.answer" type="textarea"
                                        :autosize="{ minRows: 1 }"></custom-input>
                                    <el-text class="text-content" size="large" tag="p" type="primary">知识点：</el-text>
                                    <custom-input v-model="model.knowledge" type="textarea"
                                        :autosize="{ minRows: 1 }"></custom-input>
                                </div>
                                <div v-show="model.status === 2">
                                    <el-text class="text-content" size="large" tag="p" type="warning">解析失败：</el-text>
                                    <custom-input v-model="model.answer" type="textarea" color="warning"
                                        :autosize="{ minRows: 1 }"></custom-input>
                                </div>
                                <div v-show="model.status === -1">
                                    <el-text class="text-content" size="large" tag="p" type="danger">错误：</el-text>
                                    <custom-input v-model="model.answer" type="textarea" color="danger"
                                        :autosize="{ minRows: 1 }"></custom-input>
                                </div>
                            </div>
                        </el-form-item>
                        <el-form-item>
                            <div style="width: 100%; text-align: right;">
                                <el-button type="primary" @click="solveQuestion">解题</el-button>
                                <el-button type="primary" @click="submitQuestion">提交</el-button>
                            </div>
                        </el-form-item>
                    </el-form>
                </el-scrollbar>
            </div>
        </el-card>
        <el-card class="right-content">
            <template #header>
                <div class="card-header">
                    <span>讲义</span>
                    <div>
                        <el-dropdown @command="downloadPDF">
                            <el-button :loading="loadingPDF" @click="downloadPDF('v2')" type="primary">
                                下载PDF
                                <el-icon class="el-icon--right"><arrow-down /></el-icon>
                            </el-button>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item command="v1">V1</el-dropdown-item>
                                    <el-dropdown-item command="v2">V2</el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </div>
                </div>
            </template>
            <div class="result-wrapper" v-loading="loading">
                <el-scrollbar>
                    <div v-for="(note, index) in coursenotes" :key="index" class="content-card">
                        <div class="content-card_options">
                            <el-button text>
                                <el-icon :size="15" @click="removeNote(index)">
                                    <Delete />
                                </el-icon>
                            </el-button>
                        </div>
                        <el-text v-if="note.type === 0">
                            <el-text class="text-content" size="large" tag="p" type="primary">题目：</el-text>
                            <custom-input v-model="note.question" type="textarea" :autosize="{ minRows: 1 }"></custom-input>
                            <el-text class="text-content" size="large" tag="p" type="primary">答案：</el-text>
                            <custom-input v-model="note.answer" type="textarea" :autosize="{ minRows: 1 }"></custom-input>
                            <el-text class="text-content" size="large" tag="p" type="primary">知识点：</el-text>
                            <custom-input v-model="note.knowledge" type="textarea"
                                :autosize="{ minRows: 1 }"></custom-input>
                        </el-text>
                        <el-text v-else-if="note.type === 1">
                            <el-text class="text-content" size="large" tag="p" type="primary">知识点名称：</el-text>
                            <custom-input v-model="note.knowledgeName" type="textarea"
                                :autosize="{ minRows: 1 }"></custom-input>
                            <el-text class="text-content" size="large" tag="p" type="primary">知识点：</el-text>
                            <custom-input v-model="note.knowledgeContent" type="textarea"
                                :autosize="{ minRows: 1 }"></custom-input>
                        </el-text>
                        <el-text v-else-if="note.type === 2">
                            <el-text class="text-content" size="large" tag="p" type="primary">名称：</el-text>
                            <custom-input v-model="note.name" type="textarea" :autosize="{ minRows: 1 }"></custom-input>
                            <el-text class="text-content" size="large" tag="p" type="primary">题目：</el-text>
                            <custom-input v-model="note.question" type="textarea" :autosize="{ minRows: 1 }"></custom-input>
                            <el-text class="text-content" size="large" tag="p" type="primary">正确答案：</el-text>
                            <custom-input v-model="note.correctAnswer" type="textarea"
                                :autosize="{ minRows: 1 }"></custom-input>
                            <el-text class="text-content" size="large" tag="p" type="primary">知识点：</el-text>
                            <custom-input v-model="note.knowledge" type="textarea"
                                :autosize="{ minRows: 1 }"></custom-input>
                            <el-text class="text-content" size="large" tag="p" type="primary">学生答案：</el-text>
                            <custom-input v-model="note.answer" type="textarea" :autosize="{ minRows: 1 }"></custom-input>
                            <el-text class="text-content" size="large" tag="p" type="primary">是否正确：</el-text>
                            <el-radio-group v-model="note.isTrue">
                                <el-radio :label="1">正确</el-radio>
                                <el-radio :label="0">错误</el-radio>
                            </el-radio-group>
                            <el-text class="text-content" size="large" tag="p" type="primary">错误：</el-text>
                            <custom-input v-model="note.errText" type="textarea"
                                :autosize="{ minRows: 1 }"></custom-input>
                            <el-text class="text-content" size="large" tag="p" type="primary">评价：</el-text>
                            <custom-input v-model="note.comment" type="textarea" :autosize="{ minRows: 1 }"></custom-input>
                        </el-text>
                    </div>
                </el-scrollbar>
            </div>
        </el-card>
    </div>
</template>

<script>
import ContentDialog from './content-dialog.vue'
import CustomInput from '@/components/form/custom-input.vue'

export default {
    components: {
        ContentDialog,
        CustomInput,
    },
    data() {
        return {
            type: 0, // 0-题库 1-知识点 2-作业批改
            searchType: '',
            library: {
                search: '',
            },
            libraryLoading: false,
            librarys: [],
            model: {
                aimodel: 'gpt-4',
                promot: '请回答我的问题，并提供问题所考察的知识点。\n我的问题是: ${question}',
                question: '',
                result: '',
                answer: '',
                knowledge: '',
                status: 0,
            },
            modelRules: {
                promot: [
                    { required: true, message: '请输入提示词', trigger: 'blur' }
                ],
                question: [
                    { required: true, message: '请输入题目', trigger: 'blur' }
                ],
            },
            modelLoading: false,
            coursenotes: [],
            loadingPDF: false,
        }
    },
    computed: {
        typeName() {
            return this.type === 0 ? '知识库' : '大模型'
        }
    },
    methods: {
        addContent(command) {
            if (command === 'add') {
                this.$refs.contentDialog.show();
            }
        },
        onTypeChange(typeName) {
            if (typeName === '大模型') {
                this.type = 1
            }
            if (typeName === '知识库') {
                this.type = 0
            }
        },
        onSearchChange() {
            const search = this.library.search
            if (search === '') {
                this.librarys = []
                return
            }
            this.libraryLoading = true;
            this.$axios.get(`/api/questionLibrary/findByKeyword?keyword=${search}&searchType=${this.searchType}`).then(res => {
                this.librarys = res.data
            }).finally(() => {
                this.libraryLoading = false;
            })
        },
        solveQuestion() {
            this.$refs.modelForm.validate((valid, field) => {
                if (valid) {
                    this.model = Object.assign(this.model, {
                        result: '',
                        answer: '',
                        knowledge: '',
                        status: 0,
                    })
                    this.modelLoading = true;
                    this.$axios.post(`/api/chat/completion`, this.createCompletion()).then(res => {
                        this.model.result = this.postHandleReplay(res.data.$response)
                        try {
                            let parsed = JSON.parse(this.model.result)
                            if (!parsed.answer) {
                                throw new Error()
                            }
                            this.model.status = 1
                            this.model.answer = parsed.answer
                            this.model.knowledge = parsed.knowledge
                        } catch (error) {
                            console.error(error)
                            this.model.status = 2
                            this.model.answer = this.model.result
                        }
                    }, err => {
                        this.model.status = -1
                        this.model.result = err.message
                    }).finally(() => {
                        this.modelLoading = false;
                    })
                } else {
                    this.$message.error('请完善设置信息')
                }
            })
        },
        createCompletion() {
            return {
                model: this.model.aimodel,
                messages: [
                    {
                        role: 'user',
                        content: this.replaceText(this.model.promot, { question: this.model.question }) + '\n' +
                            'Replay Latex format string for the JSON value, Do not add extra structure; Check if the JSON syntax format of the response is correct; Provide output in JSON format as follows: \n' +
                            '{"answer":"${答案}","knowledge":"${知识点}"}'
                    }
                ],
                temperature: 0.1
            }
        },
        replaceText(text, ctx) {
            return text.replace(/\${(\w+)}/g, function (match, key) {
                return ctx[key] || match;
            });
        },
        addToNotes(library) {
            this.$message.success("添加成功")
            console.log(library)
            this.coursenotes.push(library)
        },
        submitQuestion() {
            let note = {
                type: 0,
                question: this.model.question,
                answer: this.model.answer,
                knowledge: this.model.knowledge
            }
            this.coursenotes.push(note)
        },
        removeNote(index) {
            this.coursenotes.splice(index, 1)
        },
        downloadPDF(version = 'v2') {
            if (this.coursenotes.length === 0) {
                this.$message.error("请添加题目或知识点")
                return
            }
            this.loadingPDF = true
            this.$axios.post("/api/pdf/coursenote", {
                notes: this.coursenotes,
                version,
            }).then(res => {
                this.pdfUrl = this.$fileserver.fileurl(res.data.fileUrl);
                window.open(this.pdfUrl)
            }).finally(() => this.loadingPDF = false)
        },
        postHandleReplay(replay) {
            // 提取回复中的 JSON
            if (replay.startsWith('```json')) {
                replay = replay.substring(7, replay.length - 3)
            }
            if (replay.endsWith('```')) {
                replay = replay.substring(0, replay.length - 3)
            }
            return replay
        },
        libraryTypeName(type) {
            switch (type) {
                case 0:
                    return '题库'
                case 1:
                    return '知识点'
                case 2:
                    return '作业批改'
                default:
                    return ''
            }
        }
    }
}
</script>

<style lang="scss" scoped>
.container-wrapper {
    width: auto !important;
    display: flex;
    column-gap: 20px;

    .left-bar {
        width: 500px;
        max-height: calc(100vh - 100px);

        .card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .library-container {
            height: calc(100vh - 210px);
            display: flex;
            flex-direction: column;

            .search-bar {}

            :deep(.el-scrollbar) {
                height: flex 1;
            }

            .content-list {
                display: flex;
                flex-direction: column;
                row-gap: 20px;
                padding: 20px 0;

                .content-list-item {

                    :deep(.el-card__header) {
                        padding: 10px 20px;
                    }

                    :deep(.el-card__body) {
                        padding: 10px;
                    }

                    .box-card-header {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                    }
                }
            }
        }

        .model-container {

            :deep(.el-scrollbar) {
                height: calc(100vh - 210px);
            }
        }

        .model-form {
            height: calc(100vh - 210px);

            .result-card {
                width: 100%;
                padding: 10px;
                border: 1px solid var(--el-border-color);
                min-height: 200px;
            }
        }
    }

    .right-content {
        flex: 1;
        max-height: calc(100vh - 100px);

        .card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
    }

}

.result-wrapper {
    height: calc(100vh - 210px);
    display: flex;
    flex-direction: column;

    :deep(.el-scrollbar) {
        flex: 1;
    }

    .content-card {
        padding: 10px;
        border: 1px solid var(--el-border-color);
        margin-bottom: 20px;
        position: relative;

        .content-card_options {
            display: none;
            position: absolute;
            top: 5px;
            right: 5px;
        }

    }

    .content-card:hover>.content-card_options {
        display: block;
    }
}

.text-content {
    white-space: pre-wrap;
    word-break: break-all;
}
</style>