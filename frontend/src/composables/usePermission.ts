import { useAdminAuthStore } from '@/stores/adminAuth'

export function usePermission() {
  const adminAuth = useAdminAuthStore()

  /**
   * Check if the current admin has a specific permission.
   * Format: "resource:action" (e.g., "products:create")
   */
  const hasPermission = (permission: string): boolean => {
    // Superadmins have all permissions
    if (adminAuth.user?.superadmin) return true
    
    // If not superadmin, check permissions list
    // Note: This expects the user object to have a 'permissions' array
    // which should be populated by the backend in the AuthTokenResponse or a separate /me call.
    if (!adminAuth.user?.permissions) return false
    
    return adminAuth.user.permissions.includes(permission)
  }

  /**
   * Check if the admin has ALL given permissions
   */
  const hasAllPermissions = (permissions: string[]): boolean => {
    return permissions.every(p => hasPermission(p))
  }

  /**
   * Check if the admin has ANY of the given permissions
   */
  const hasAnyPermission = (permissions: string[]): boolean => {
    if (adminAuth.user?.superadmin) return true
    return permissions.some(p => hasPermission(p))
  }

  return {
    hasPermission,
    hasAllPermissions,
    hasAnyPermission,
    isSuperadmin: adminAuth.user?.superadmin || false
  }
}
