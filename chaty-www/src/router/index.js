import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/store/index";

const router = createRouter({
	history: createWebHistory(import.meta.env.BASE_URL),
	routes: [
		{
			path: "/",
			redirect: "/home", // 修改默认跳转到 /home
		},
		{
			path: "/",
			component: () => import("../views/common/layout.vue"),
			props: true,
			children: [
				{
					path: "/review",
					component: () => import("../views/grade/index.vue"),
					meta: { keepAlive: true },
				},
				{
					path: "/docreview",
					component: () => import("../views/docreview/index.vue"),
					meta: { keepAlive: true },
					props: true,
				},
				{
					name: "docreview-list",
					path: "reviewlist",
					component: () =>
						import("../views/docreview/reviewlist.vue"),
					meta: { keepAlive: true },
					props: true,
				},
				{
					path: "/imgocr",
					component: () => import("../views/imgocr/index.vue"),
					meta: { keepAlive: true },
				},
				{
					path: "/library",
					component: () => import("../views/library/index.vue"),
					meta: { keepAlive: true },
					children: [
						{
							path: "question",
							component: () =>
								import("../views/question/index.vue"),
							meta: { keepAlive: false },
						},
						{
							path: "knowledge",
							component: () =>
								import("../views/knowledge/index.vue"),
							meta: { keepAlive: false },
						},
						{
							path: "review",
							component: () =>
								import("../views/review/index.vue"),
							meta: { keepAlive: false },
						},
						{
							path: "docreviewrec",
							component: () => import("../views/docreviewrec/index.vue"),
							meta: { keepAlive: false }
						},
					],
				},
				{
					path: "/coursenote",
					component: () => import("../views/coursenote/index.vue"),
					meta: { keepAlive: true },
				},
				{
					path: "/solve",
					component: () => import("../views/solve/index.vue"),
					meta: { keepAlive: true },
				},
				{
					path: "/chat",
					component: () => import("../views/chat/index.vue"),
					meta: { keepAlive: true },
				},
				{
					path: "/doccorrect",
					component: () => import("../views/doccorrect/task.vue"),
					meta: { keepAlive: true },
				},
				{
					path: "/docconfig",
					name: "docconfig",
					component: () => import("../views/doccorrect/config.vue"),
					meta: { keepAlive: true },
					props: true,
				},
				{
					path: "/docrecord/:taskId",
					component: () => import("../views/doccorrect/record.vue"),
				},
				{
					path: "/docfile",
					name: "docfile",
					component: () => import("../views/doccorrect/file.vue"),
				},
				{
					path: "/docfile/:fileId",
					name: "docfile-task",
					component: () => import("../views/doccorrect/filetask.vue"),
				},
				{
					path: "/markPapers",
					name: "markPapers",
					component: () => import("../views/markPapers/index.vue"),
				},
				{
					path: "/markPapers/stepMarkPapers/:fileId",
					name: "stepMarkPapers",
					component: () => import("../views/markPapers/components/stepPage.vue"),
				},

				{
					path: "/stats",
					name: "stats",
					component: () => import("../views/stats/Stats.vue"),
				},
				{
					path: "/correctConfigPackages",
					name: "correctConfigPackages",
					component: () => import("../views/correctconfigpackages/index.vue"),
				},
				{
					path: "/correctConfigPackages/stepConfigPapers/:id",
					name: "stepConfigPapers",
					component: () => import("../views/correctconfigpackages/components/stepPage.vue"),
				},
				{
					path: "/essayPapers",
					name: "essayPapers",
					component: () => import("../views/essay/index.vue"),
				},
				{
					path: "/essayPapers/stepEssayPapers/:fileId",
					name: "stepEssayPapers",
					component: () => import("../views/essay/components/stepPage.vue"),
				},
				{
					path: "/class/config",
					name: "classConfig",
					component: () => import("../views/class/index.vue"),
				},
				{
					path: "/comprehensive",
					name: "comprehensive",
					component: () => import("../views/comprehensive/index.vue"),
				},
			],
		},
		{
			path: "/doc",
			redirect: "/doc/generate",
			component: () => import("../views/common/layout1.vue"),
			props: true,
			children: [
				{
					path: "/doc/generate",
					component: () => import("../views/gendoc/index.vue"),
					meta: { keepAlive: true },
				},
				{
					path: "/doc/review",
					component: () => import("../views/doc/review/index.vue"),
					meta: { keepAlive: true },
				}
			]
		},
		{
			path: "/login",
			component: () => import("../views/login/index.vue")
		},
		{
			path: "/home",
			component: () => import("../views/home/index.vue")
		},
		{
			path: '/en',
			name: 'English',
			beforeEnter: (to, from, next) => {
				useUserStore().setLocale('en')
				next('/');
			}
		}

	],
});

export default router;
