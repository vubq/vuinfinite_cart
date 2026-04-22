# VuInfinite Cart - Architecture & Structure

This document outlines the high-level architecture and current implementation structure of the VuInfinite Cart platform.

**Last Updated**: 2026-04-22 (Admin Console Data UX & Shared UI Patterns)

## 1. System Overview

VuInfinite Cart is a full-stack e-commerce platform supporting both B2C customers and internal admin operations.
It is designed to support generic catalog / variant / SKU-based commerce flows.

- **Backend**: Java 17, Spring Boot 3.3.5, PostgreSQL, Redis, Flyway, Spring Security, OAuth2, JWT
- **Frontend**: Vue 3, TypeScript, Vite, Tailwind CSS v4, Pinia, Vue Router

Current implementation focus:
- Admin management console is the most actively implemented product area.
- Shared front-end primitives now cover loading, select, pagination, and confirmation flows.
- Admin list screens already support search, filter, pagination, and sort via server-side `Pageable`.

## 2. Authentication & Security Architecture

The system uses a dual-domain authentication strategy so admin and customer sessions can coexist in the same browser.

### Customer Auth

- OAuth2 with Google / Facebook
- Access token: JWT in Pinia memory, 15 minutes
- Refresh token: `HttpOnly` cookie `customer_rt` on `/api/auth/customer/refresh`, 7 days

### Admin Auth

- Internal username / password
- Access token: JWT in Pinia memory, 15 minutes
- Refresh token: `HttpOnly` cookie `admin_rt` on `/api/auth/admin/refresh`, 1 day

### Important Implementation Notes

- Refresh tokens are whitelisted in Redis for revocation support.
- Customer refresh tokens are revoked immediately on ban.
- Admin refresh tokens are revoked immediately on logout.
- Admin JWTs embed aggregated permission keys for frontend gating.

## 3. Directory Structure

```plaintext
VuInfinite_Cart/
|-- backend/
|   |-- src/main/java/vuinfinitecart/web/
|   |   |-- admin/              # Admin management domain
|   |   |-- auth/               # Admin + customer authentication
|   |   |-- cart/               # Shopping cart domain
|   |   |-- common/             # Shared response, exception, audit, security utilities
|   |   |-- config/             # Spring config, JWT props, CORS, security
|   |   |-- coupon/             # Discount logic
|   |   |-- customer/           # Customer management domain
|   |   |-- i18n/               # Translation APIs and services
|   |   |-- order/              # Order domain
|   |   |-- payment/            # Payment domain
|   |   |-- permission/         # RBAC permissions and groups
|   |   |-- product/            # Catalog domain
|   |   `-- review/             # Review domain
|   `-- src/main/resources/
|       |-- application.yml
|       `-- db/migration/
|
|-- frontend/
|   |-- src/
|   |   |-- api/                # Axios clients and service wrappers
|   |   |-- assets/             # Static assets and base styles
|   |   |-- components/
|   |   |   |-- shared/         # Business-aware components
|   |   |   `-- ui/             # Reusable design-system primitives
|   |   |                      # VButton, VInput, VModal, VCard
|   |   |                      # VSelect, VPagination, VConfirmDialog, VLoadingBar
|   |   |-- composables/        # Shared logic hooks
|   |   |-- layouts/            # AdminLayout, CustomerLayout
|   |   |-- router/             # Application routes and guards
|   |   |-- stores/             # Pinia stores
|   |   |                      # adminAuth, customerAuth, i18n, app
|   |   `-- views/
|   |       |-- admin/          # Admin screens
|   |       `-- customer/       # Customer screens
|   `-- package.json
|
|-- PROJECT_STRUCTURE.md        # High-level architecture document
`-- SYSTEM_GUIDE.md             # Fast handover guide for engineers / models
```

## 4. RBAC Model

Admin permissions use `resource:action` keys such as:
- `products:create`
- `orders:read`
- `admins:update`

Roles and permission behavior:
- **Superadmins** bypass all normal permission checks
- **Permission Groups** provide reusable grouped permissions
- **Individual Permissions** act as targeted add-ons / overrides

Effective admin permission set:
- `group permissions UNION individual permissions`

Current admin console capabilities:
- invite admin accounts
- assign permission groups and individual permissions
- suspend / restore admin access
- create / update / delete permission groups
- restrict / restore customer accounts

## 5. UI / UX Design Philosophy

The UI follows a luxury minimalist direction.

### Admin Panel

- Zen Emerald aesthetic
- soft slate borders
- whitespace-first layout
- subtle hover affordances
- emerald reserved for active or confirmatory emphasis

### Storefront

- premium retail tone
- typography and imagery first

### Shared Interaction Rules

- destructive or access-changing actions should use a confirmation dialog
- custom select components are preferred over browser-native select styling
- pagination should feel like part of the table footer, not a detached dashboard block
- list pages should prefer server-side pagination and sorting

## 6. i18n Strategy

Dynamic i18n logic:
- translations are stored in PostgreSQL
- cache is backed by Redis
- updates invalidate cache and become available without redeployment

Seed structure:
- `V7__create_i18n_tables.sql`

## 7. API & Frontend Integration Notes

Important conventions:
- use `ApiResponse<T>` for normal responses
- use `ApiResponse<PageResponse<T>>` for paginated list endpoints
- Spring `Pageable` is the preferred list-query interface for admin screens
- frontend API clients attach bearer tokens automatically
- frontend API clients attempt refresh on `401`
- global loading state is managed centrally and powers the loading bar

Practical warning:
- some older flows may still assume mixed raw / wrapped response shapes, so verify response format before extending a screen

## 8. Shared Frontend Patterns Now In Use

Reusable UI primitives currently considered standard:
- `VSelect`
- `VPagination`
- `VConfirmDialog`
- `VLoadingBar`

Expected behavior:
- `VSelect` renders a custom dropdown via Teleport and adapts to viewport space
- `VPagination` supports rows-per-page and server-driven page navigation
- `VConfirmDialog` is the preferred pre-action confirmation pattern

When building new admin CRUD screens, reuse these primitives instead of creating one-off behavior.

## 9. Immediate Caveats

- The admin console is the strongest reference implementation in the repository right now.
- Permission changes apply immediately on the backend, but currently logged-in frontend state can still depend on the active JWT lifecycle.
- Sorting should only target safe mapped entity fields when using Spring `Pageable`.
- Dropdowns near viewport edges should be viewport-aware or Teleport-based.
