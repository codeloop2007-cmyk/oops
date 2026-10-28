Expo Starter Template

    A production-ready Expo template with TypeScript, Orval API codegen, pre-configured Axios, tab-based navigation, and built-in AI agent support for Claude Code, Cursor, and OpenAI Codex.

✨ Features

    Expo SDK: Latest stable Expo SDK with React Native support
    TypeScript: Strict mode enabled, full type safety across the codebase
    Orval Integration: Auto-generate type-safe API clients and hooks from OpenAPI specs via orval.config.ts
    Pre-configured Axios: Centralized API client with interceptors in src/api/axios.ts
    Expo Router: File-based routing with screens in src/app/
    Tab Navigation: Pre-built Home and Explore tabs with icons in assets/images/tabIcons/
    Environment Config: Type-safe environment variables via src/config/env.config.ts and .env.example
    AI Agent Ready: Pre-configured CLAUDE.md, AGENTS.md, .claude/settings.json, and .vscode/ recommendations
    Expo Assets: Adaptive icons, splash screens, and tab icons pre-included in assets/
    VSCode Support: Recommended extensions and settings in .vscode/

📋 Prerequisites

    Node.js 18+
    pnpm (recommended) or npm/yarn
    Expo CLI: npm install -g expo-cli
    EAS CLI (for production builds): npm install -g eas-cli
    Orval CLI (for API codegen): npm install -g orval
    iOS Simulator (macOS) or Android Emulator (optional)

🚀 Quick Start

    Clone the repository:

    git clone https://github.com/codeloop2007-cmyk/expo-template.git
    cd expo-template

    Install dependencies:

    pnpm install
    # or npm install / yarn install

    Set up environment variables:

    cp .env.example .env
    # Edit .env with your local values

    Generate API types (if using Orval):

    pnpm run generate:api

    Start the development server:

    pnpm start

    Run on a device/emulator:
        iOS: pnpm run ios
        Android: pnpm run android
        Web: pnpm run web
        Physical device: Scan the QR code with the Expo Go app

📂 Project Structure

Exact structure from the uploaded expo-template-main.zip:

expo-template/
├── .claude/ # Claude Code configuration
│ └── settings.json
├── .vscode/ # VSCode recommendations
│ ├── extensions.json
│ └── settings.json
├── assets/ # Static assets
│ ├── expo.icon/ # Expo adaptive icon set
│ │ ├── Assets/
│ │ │ ├── expo-symbol 2.svg
│ │ │ └── grid.png
│ │ └── icon.json
│ └── images/ # App images and icons
│ ├── tabIcons/ # Tab navigation icons (home, explore)
│ ├── android-icon-_.png
│ ├── expo-badge_.png
│ ├── icon.png
│ └── splash-icon.png
├── context/ # AI agent context files (added for agent support)
│ ├── project-overview.md
│ ├── architecture.md
│ ├── code-standards.md
│ ├── ai-workflow-rules.md
│ └── progress-tracker.md
├── specs/ # Feature specifications for AI agents (added)
├── src/ # All application source code
│ ├── api/ # API-related code
│ │ ├── axios.ts # Pre-configured Axios instance
│ │ └── generated/ # Orval-generated types/hooks (auto-generated)
│ ├── app/ # Expo Router screens
│ │ ├── \_layout.tsx # Root layout with tab navigator
│ │ └── index.tsx # Home tab screen
│ └── config/ # Configuration files
│ └── env.config.ts # Type-safe environment variables
├── .env.example # Environment variable example
├── .gitignore
├── AGENTS.md # AI agent entry point
├── app.json # Expo configuration
├── CLAUDE.md # Claude Code-specific instructions
├── LICENSE
├── orval.config.ts # Orval API codegen configuration
├── package.json
├── tsconfig.json
└── README.md

📜 Available Scripts

Assumed based on standard Expo + Orval setups (verify with package.json):
Script Description
pnpm start Start Expo dev server
pnpm run ios Run on iOS simulator
pnpm run android Run on Android emulator
pnpm run web Run in web browser
pnpm run generate:api Generate API types with Orval
pnpm run lint Run ESLint
pnpm run type-check Run TypeScript type check
pnpm run build:android EAS build for Android
pnpm run build:ios EAS build for iOS
🤖 AI Agent Support

This template is pre-configured for AI coding agents:

    Claude Code: Uses .claude/settings.json and CLAUDE.md
    Cursor: Add .cursor/rules/ (see AI Instructions section)
    OpenAI Codex: Uses AGENTS.md as entry point

All agents must read the context/ folder files first before making changes. See docs/ai-integration.md for full details.
🤝 Contributing

    Fork the repository
    Create a feature branch: git checkout -b feat/your-feature
    Commit changes: git commit -m "feat: add your feature"
    Push to branch: git push origin feat/your-feature
    Open a pull request

📄 License

MIT License — see LICENSE file for details.
