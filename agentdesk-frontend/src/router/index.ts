import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/auth/Login.vue'),
      meta: { noAuth: true }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/auth/Register.vue'),
      meta: { noAuth: true }
    },
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      redirect: '/chat',
      children: [
        {
          path: 'chat',
          name: 'Chat',
          component: () => import('@/views/chat/Chat.vue')
        },
        {
          path: 'tickets/create',
          name: 'TicketCreate',
          component: () => import('@/views/tickets/TicketCreate.vue')
        },
        {
          path: 'tickets/:id',
          name: 'TicketDetail',
          component: () => import('@/views/tickets/TicketDetail.vue')
        },
        {
          path: 'tickets',
          name: 'Tickets',
          component: () => import('@/views/tickets/TicketList.vue')
        },
        {
          path: 'knowledge/create',
          name: 'KnowledgeCreate',
          component: () => import('@/views/knowledge/KnowledgeEdit.vue')
        },
        {
          path: 'knowledge/:id/edit',
          name: 'KnowledgeEdit',
          component: () => import('@/views/knowledge/KnowledgeEdit.vue')
        },
        {
          path: 'knowledge',
          name: 'Knowledge',
          component: () => import('@/views/knowledge/KnowledgeList.vue')
        },
        {
          path: 'admin/dashboard',
          name: 'AdminDashboard',
          component: () => import('@/views/admin/Dashboard.vue'),
          meta: { roles: ['ADMIN'] }
        },
        {
          path: 'admin/audit',
          name: 'AdminAudit',
          component: () => import('@/views/admin/AuditLog.vue'),
          meta: { roles: ['ADMIN'] }
        },
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('@/views/profile/Profile.vue')
        }
      ]
    }
  ]
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('accessToken')
  if (to.meta.noAuth) {
    if (token) {
      next('/chat')
    } else {
      next()
    }
  } else {
    if (!token) {
      next('/login')
    } else {
      next()
    }
  }
})

export default router
