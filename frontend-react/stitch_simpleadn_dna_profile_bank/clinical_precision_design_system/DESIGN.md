---
name: Clinical Precision Design System
colors:
  surface: '#f9f9f9'
  surface-dim: '#dadada'
  surface-bright: '#f9f9f9'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f3f3'
  surface-container: '#eeeeee'
  surface-container-high: '#e8e8e8'
  surface-container-highest: '#e2e2e2'
  on-surface: '#1a1c1c'
  on-surface-variant: '#3f484f'
  inverse-surface: '#2f3131'
  inverse-on-surface: '#f1f1f1'
  outline: '#6f7880'
  outline-variant: '#bec8d0'
  surface-tint: '#00658d'
  primary: '#00628a'
  on-primary: '#ffffff'
  primary-container: '#007dad'
  on-primary-container: '#fcfcff'
  inverse-primary: '#83cfff'
  secondary: '#aa3243'
  on-secondary: '#ffffff'
  secondary-container: '#fd707f'
  on-secondary-container: '#70011e'
  tertiary: '#535e63'
  on-tertiary: '#ffffff'
  tertiary-container: '#6b767b'
  on-tertiary-container: '#fafdff'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#c6e7ff'
  primary-fixed-dim: '#83cfff'
  on-primary-fixed: '#001e2e'
  on-primary-fixed-variant: '#004c6c'
  secondary-fixed: '#ffdadb'
  secondary-fixed-dim: '#ffb2b6'
  on-secondary-fixed: '#40000d'
  on-secondary-fixed-variant: '#89192e'
  tertiary-fixed: '#d9e4ea'
  tertiary-fixed-dim: '#bdc8ce'
  on-tertiary-fixed: '#131d21'
  on-tertiary-fixed-variant: '#3e484d'
  background: '#f9f9f9'
  on-background: '#1a1c1c'
  surface-variant: '#e2e2e2'
typography:
  headline-xl:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.05em
  label-sm:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 14px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  container-max: 1440px
  gutter: 24px
  margin-page: 48px
  stack-sm: 8px
  stack-md: 16px
  stack-lg: 24px
---

## Brand & Style
The brand personality is rooted in scientific rigor and clinical accessibility. It aims to evoke feelings of security, modernism, and clarity—essential for a DNA profile bank handling sensitive genomic data. 

The design style is **Corporate / Modern** with a focus on high-legibility and spatial organization. It utilizes a layered approach to separate administrative tasks from user data views. A distinctive feature is the subtle DNA double-helix watermark, applied at 5-8% opacity, which acts as a structural motif rather than a mere decoration, grounding the interface in its biological purpose. The visual language balances the authority of a medical laboratory with the approachability of a contemporary SaaS application.

## Colors
The color palette is functionally driven by semantic clarity.
- **Primary Blue (#2b94c8):** Used for constructive actions ("Aprobar"), active navigation states, and primary brand headers. It represents trust and professional stability.
- **Soft Blue (#eaf5fb):** Acts as a high-frequency background tint for cards and hover states, providing a cooling effect that softens the clinical white.
- **Soft Coral (#e25c6b):** Reserved strictly for negative or destructive actions ("Rechazar", "Inactivo"). It is intentionally bright and soft rather than dark or aggressive to maintain a friendly, approachable tone even in error states.
- **Canvas Gray (#f3f3f3):** The base application surface, providing a low-contrast foundation that makes white containers (cards) and blue accents pop.

## Typography
This design system utilizes **Inter** for its exceptional legibility and systematic character, ideal for data-heavy genomic applications. 

Hierarchy is established through weight shifts rather than extreme size changes. Headlines use a tighter letter-spacing to appear more authoritative. Labels are often used for metadata (e.g., sample IDs, timestamps) and follow a structured, sometimes uppercase format to differentiate from user-generated content. All body text maintains a generous line height to ensure readability of long DNA sequences or clinical reports.

## Layout & Spacing
The layout follows a **Fixed Grid** philosophy optimized for desktop viewports (>=1280px). 
- **TopBar:** Spans the full width of the screen but content is constrained to a 1440px max-width. It uses a multi-column layout to house navigation, user profile, and global search without the need for hidden menus.
- **Grid:** A 12-column grid system is used for the main canvas. Cards typically span 3, 4, or 6 columns depending on the data density.
- **Responsive Behavior:** On smaller screens (tablets), the 12-column grid collapses to 6 columns. On desktop, the side-by-side layout for cards is mandatory to allow for quick comparisons between DNA profiles.
- **Spacing:** An 8px base unit drives all padding and margins, ensuring a rhythmic vertical flow.

## Elevation & Depth
Depth is used as an interactive signal rather than purely aesthetic ornamentation.
- **Base Surface:** Flat background (#f3f3f3).
- **Resting State:** Cards and containers have no shadow but feature a subtle 1px border (#e0e0e0) or a soft blue background (#eaf5fb).
- **Interactive State (Hover):** When hovered, cards lift using a soft, diffused ambient shadow: `0 10px 25px -5px rgba(43, 148, 200, 0.1)`. The shadow is slightly tinted with the primary blue to maintain color harmony.
- **Modals:** Use a higher elevation with a backdrop blur (12px) to focus the user's attention on the task at hand, whether it's a confirmation or a complex data entry form.

## Shapes
The shape language is consistently **Rounded** (0.5rem / 8px). This radius is applied to cards, input fields, and buttons. 

A specific exception is made for **Status Chips**, which use the `rounded-xl` (1.5rem / 24px) setting to create a "Pill" shape. This distinct geometry helps status indicators stand out from functional buttons or data containers, allowing users to scan for "Active" vs "Inactive" states at a glance.

## Components
- **Buttons:** Primary buttons use a solid #2b94c8 fill with white text. Destructive buttons use a #e25c6b fill. Both feature a smooth transition `cubic-bezier(0.4, 0, 0.2, 1)` on hover, slightly darkening the fill.
- **Inputs:** Floating labels are mandatory. On focus, the input border changes to the primary blue with a 4px "glow" (a soft blue box-shadow) to clearly indicate the active field.
- **Status Chips:** 
    - *Active:* #eaf5fb background with #2b94c8 text.
    - *Inactive:* Light Coral background with #e25c6b text (low opacity background, full opacity text).
- **Cards:** Side-by-side layout containers. White background, 8px corner radius. On hover, they lift and the background transitions to a very faint #eaf5fb.
- **Modals:**
    - *Confirmation:* Small (400px wide), center-aligned, icon-driven (e.g., a blue check or coral alert).
    - *Form:* Wide (800px), multi-column layout for biological data entry.
- **TopBar:** High-contrast white background, 72px height, featuring the DNA watermark motif integrated into the far-right background.
- **Transitions:** All state changes (hover, focus, modal entry) must use the defined cubic-bezier for a "scientific/precision" feel—fast yet smooth.