# VuInfinite Cart - System Guide

This guide is intended to help another engineer or model become productive in the repository quickly.

## 1. Fast Summary

The most mature part of the system today is the admin console.

Already implemented in meaningful depth:
- admin authentication
- customer authentication
- RBAC with permission groups and individual permissions
- admin management views
- customer account restriction / restore flows
- reusable admin UI primitives for select, pagination, loading, and confirmation dialogs

If you need a reference implementation for a new management screen, start with the admin views.

## 2. Backend Rules

Prefer these conventions:
- normal endpoints return `ApiResponse<T>`
- paginated endpoints return `ApiResponse<PageResponse<T>>`
- use Spring `Pageable` for admin list pages that need page, size, and sort
- keep status-changing operations explicit and auditable in intent

Security reminders:
- admin and customer auth are separate
- refresh tokens live in `HttpOnly` cookies
- access tokens are bearer tokens managed by the SPA
- Redis handles refresh-token revocation semantics

RBAC reminders:
- superadmin bypass exists
- effective permissions = group union + individual permission union
- admin JWT embeds permissions for UI-level checks

## 3. Frontend Rules

Architecture:
- Vue 3 + TS + Vite + Pinia + Vue Router
- shared API layer in `frontend/src/api`
- shared UI layer in `frontend/src/components/ui`

Interaction rules:
- do not use browser-native `confirm` for important actions
- use `VConfirmDialog`
- do not style browser-native select directly for admin UI
- use `VSelect`
- if a dropdown can open near the bottom of the viewport, rely on the existing viewport-aware dropdown behavior

Table rules:
- prefer server-side pagination
- prefer server-side sorting
- keep pagination visually attached to the table footer
- keep table headers, filters, and table actions consistent across admin pages

## 4. Shared UI Primitives To Reuse

Use these before inventing new behavior:
- `VButton`
- `VInput`
- `VModal`
- `VCard`
- `VSelect`
- `VPagination`
- `VConfirmDialog`
- `VLoadingBar`

Behavior expectations:
- `VSelect` uses a custom dropdown, Teleport rendering, and adaptive open direction
- `VPagination` is designed for server-driven list pages
- `VConfirmDialog` is the standard confirmation UX for create / update / delete / suspend / restore / ban actions

## 5. Current Admin UX Conventions

Visual direction:
- luxury minimalist
- emerald accents
- slate borders
- light hover states
- whitespace-first spacing

Behavior direction:
- destructive actions need confirmation
- status changes need confirmation
- list pages should expose search, filter, sort, and page size in a consistent way

## 6. Common Gotchas

- mixed response-shape assumptions can cause empty UI even when the API returns data
- permission changes are immediate on the backend, but frontend visibility can still depend on token lifecycle
- Spring `Pageable` sorting should only use safe mapped entity fields
- dropdowns and overlays should avoid changing layout flow when opened

## 7. Recommended Pattern For A New Admin CRUD Screen

Preferred stack:
1. Backend list endpoint with `Pageable`
2. Backend write endpoints wrapped in `ApiResponse`
3. Frontend screen with:
   - search
   - optional status filter via `VSelect`
   - sortable table headers
   - `VPagination`
   - `VConfirmDialog` for important writes

## 8. Best Files To Read First

Recommended reading order for a new contributor:
1. `PROJECT_STRUCTURE.md`
2. `SYSTEM_GUIDE.md`
3. `frontend/src/views/admin/AdminsView.vue`
4. `frontend/src/views/admin/RolesView.vue`
5. `frontend/src/views/admin/CustomersView.vue`
6. `backend/src/main/java/vuinfinitecart/web/admin`
7. `backend/src/main/java/vuinfinitecart/web/permission`
