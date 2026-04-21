# VuInfinite Cart - Architecture & Structure

This document outlines the high-level architecture and directory structure of the VuInfinite Cart platform.
**Last Updated**: 2026-04-21 (RBAC & UI Polish Phase)

## 1. System Overview

VuInfinite Cart is a full-stack e-commerce platform supporting both B2C (Customer) and internal management (Admin).
It is designed to sell any type of product, utilizing variant/SKU models.

- **Backend**: Java 17, Spring Boot 3.3.5, PostgreSQL, Redis, Flyway, Spring Security (OAuth2 + JWT).
- **Frontend**: Vue 3, TypeScript, Vite, Tailwind CSS v4, Pinia, Vue Router.

## 2. Authentication & Security Architecture

The system uses a **Dual-Domain Authentication Strategy** to allow simultaneous Customer and Admin logins on the same browser seamlessly.

- **Customer Auth**:
  - Uses Google/Facebook OAuth2.
  - Access Token (JWT): In-memory (Pinia) — 15 mins.
  - Refresh Token: `HttpOnly` cookie (`customer_rt`) on `/api/auth/customer/refresh` — 7 days.
- **Admin Auth**:
  - Internal Username/Password (BCrypt).
  - Access Token (JWT): In-memory (Pinia) — 15 mins.
  - Refresh Token: `HttpOnly` cookie (`admin_rt`) on `/api/auth/admin/refresh` — 1 day.

Both sets of refresh tokens are **whitelisted in Redis**. This allows instant revocation (logout/ban) without waiting for token expiration.

## 3. Directory Structure

```plaintext
VuInfinite_Cart/
├── backend/                       # Spring Boot Application (Gradle)
│   ├── src/main/java/vuinfinitecart/web/
│   │   ├── admin/                 # Admin bounded context (Users, Role assignments)
│   │   ├── auth/                  # Authentication controllers & services
│   │   │   ├── admin/
│   │   │   └── customer/
│   │   ├── cart/                  # Shopping cart (Guest & Authenticated)
│   │   ├── common/                # Shared utilities (Exception, Response, Audit, Security)
│   │   ├── config/                # Spring Boot Configs (CORS, Security, Props)
│   │   ├── coupon/                # Discount logic
│   │   ├── customer/              # Customer bounded context (Profile, Addresses)
│   │   ├── i18n/                  # Dynamic translations (Redis-backed)
│   │   ├── order/                 # Order management & status tracking
│   │   ├── payment/               # Payment gateway integrations
│   │   ├── permission/            # Fine-grained RBAC entities
│   │   ├── product/               # Catalog, Variants, SKUs
│   │   └── review/                # Product reviews
│   └── src/main/resources/
│       ├── application.yml        # Main config
│       └── db/migration/          # Flyway SQL migrations (V1 - V7)
│
├── frontend/                      # Vue 3 SPA (Vite)
│   ├── src/
│   │   ├── assets/                # Static assets & Tailwind base (.css)
│   │   ├── components/            # Vue Components
│   │   │   ├── ui/                # Base Design System (VButton, VInput, VModal, VCard)
│   │   │   └── shared/            # Business-aware components
│   │   ├── layouts/               # CustomerLayout & AdminLayout (Sidebar/TopNav)
│   │   ├── views/                 # Page-level components
│   │   │   └── admin/             # Admin Management Views (AdminsView, RolesView)
│   │   ├── router/                # Centrally managed routes with Guards
│   │   ├── stores/                # Pinia state (adminAuth, i18n)
│   │   ├── composables/           # Shared logic (usePermissions)
│   │   └── api/                   # Axios clients & service wrappers
│   └── package.json
└── PROJECT_STRUCTURE.md           # This file
```

## 4. RBAC (Role-Based Access Control)

Admin permissions are managed at a fine-grained level using `resource:action` format (e.g., `products:create`, `orders:read`).
- **Superadmins**: Bypass all checks (`is_superadmin = true`). Managed exclusively by root.
- **Permission Groups**: Departmental clusters (e.g., "Logistics", "Marketing") assigned to admins to grant bulk permissions.
- **Individual Permissions**: "Override" or "Add-on" permissions granted directly to an admin account for special tasks.

The system performs an **Aggregated Permission Check** (Group Perms ∪ Individual Perms) to determine total access. Logic is optimized with Eager Loading (LEFT JOIN FETCH) to prevent N+1 issues.

## 5. UI/UX Design Philosophy

The platform follows a **"Luxury Minimalist"** design system using Tailwind CSS:
- **Admin Panel**: "Zen Emerald" aesthetic. Focus on high whitespace, subpixel borders (Slate 100), and Emerald accents for indicators.
- **Storefront**: Premium Retail approach focusing on typography and product imagery.
- **Component Pillars**: Reusable `VComponent` set ensures perfect consistency in layouts and interactions.

## 6. i18n Strategy

Dynamic i18n logic:
- Translations stored in PostgreSQL (`V7__create_i18n_tables.sql`).
- Cached in Redis.
- Updates push invalidation to Redis, propagating to frontend instantly upon page load or manual refresh, removing the need for application redeployment when copy changes.
