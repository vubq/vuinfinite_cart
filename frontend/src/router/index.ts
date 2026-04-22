import { createRouter, createWebHistory } from 'vue-router'
import CustomerLayout from '@/layouts/CustomerLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import { useAdminAuthStore } from '@/stores/adminAuth'
import { useCustomerAuthStore } from '@/stores/customerAuth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // Customer Routes
    {
      path: '/',
      component: CustomerLayout,
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('@/views/customer/HomeView.vue')
        },
        {
          path: 'login',
          name: 'login',
          component: () => import('@/views/customer/LoginView.vue'),
          meta: { guestOnly: true }
        },
        {
          path: 'register',
          name: 'register',
          component: () => import('@/views/customer/RegisterView.vue'),
          meta: { guestOnly: true }
        }
      ]
    },
    // Admin Routes
    {
      path: '/admin',
      component: AdminLayout,
      children: [
        {
          path: '',
          name: 'admin-dashboard',
          component: () => import('@/views/admin/DashboardView.vue'),
          meta: { requiresAdmin: true }
        },
        {
          path: 'products',
          name: 'admin-products',
          component: () => import('@/views/admin/ProductsView.vue'),
          meta: { requiresAdmin: true }
        },
        {
          path: 'orders',
          name: 'admin-orders',
          component: () => import('@/views/admin/OrdersView.vue'),
          meta: { requiresAdmin: true }
        },
        {
          path: 'customers',
          name: 'admin-customers',
          component: () => import('@/views/admin/CustomersView.vue'),
          meta: { requiresAdmin: true }
        },
        {
          path: 'media',
          name: 'admin-media',
          component: () => import('@/views/admin/MediaView.vue'),
          meta: { requiresAdmin: true }
        },
        {
          path: 'system',
          name: 'admin-admins',
          component: () => import('@/views/admin/AdminsView.vue'),
          meta: { requiresAdmin: true }
        },
        {
          path: 'system/roles',
          name: 'admin-roles',
          component: () => import('@/views/admin/RolesView.vue'),
          meta: { requiresAdmin: true }
        },
        {
          path: 'login',
          name: 'admin-login',
          component: () => import('@/views/admin/LoginView.vue'),
          meta: { guestOnly: true }
        }
      ]
    }
  ]
})

router.beforeEach((to, _from, next) => {
  const adminAuth = useAdminAuthStore()
  const customerAuth = useCustomerAuthStore()

  // Guard for Admin routes
  if (to.meta.requiresAdmin && !adminAuth.isAuthenticated) {
    return next({ name: 'admin-login' })
  }

  // Guard for Guest routes (Login/Register)
  if (to.meta.guestOnly) {
    if (to.path.startsWith('/admin') && adminAuth.isAuthenticated) {
      return next({ name: 'admin-dashboard' })
    }
    if (!to.path.startsWith('/admin') && customerAuth.isAuthenticated) {
      return next({ name: 'home' })
    }
  }

  next()
})

export default router
