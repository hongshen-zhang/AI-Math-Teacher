import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
	history: createWebHistory(import.meta.env.BASE_URL),
	routes: [
		{
			path: "/",
			component: () => import("../views/common/layout.vue"),
			children: [
				{
					path: "",
					component: () => import("../views/grade/index.vue"),
					meta: { keepAlive: true },
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
							component: () => import("../views/question/index.vue"),
							meta: { keepAlive: false }
						},
						{
							path: 'knowledge',
							component: () => import("../views/knowledge/index.vue"),
							meta: { keepAlive: false }
						},
						{
							path: 'review',
							component: () => import("../views/review/index.vue"),
							meta: { keepAlive: false }
						}
					]
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
			],
		},
	],
});

export default router;
