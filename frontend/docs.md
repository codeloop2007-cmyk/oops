Create a docs/ directory in the repo root with the following files:
docs/getting-started.md
Getting Started
Detailed Setup

    Clone the repo: Follow the Quick Start section in the README.
    Environment Variables:
        Copy .env.example to .env
        Update src/config/env.config.ts if adding new variables
        Never commit .env to git
    Orval API Setup:
        Place your OpenAPI spec (e.g., openapi.json) in the project root
        Update orval.config.ts to point to your spec
        Run pnpm run generate:api to create type-safe API hooks in src/api/generated/

First Run

After starting the dev server, you will see the pre-built Home tab. The Explore tab is pre-configured but may require additional implementation.
Customization

    Add new tabs: Update src/app/_layout.tsx and add tab icons to assets/images/tabIcons/
    Add components: Create new files in src/components/
    Add API endpoints: Update your OpenAPI spec and regenerate types with Orval

docs/api-guide.md
API Guide
Pre-configured Axios Instance

src/api/axios.ts provides a centralized Axios client with:

    Base URL from environment variables
    Request/response interceptors (add auth headers here)
    Error handling

Example usage:

import axios from '@/src/api/axios';

const fetchUser = async () => {
const response = await axios.get('/users/me');
return response.data;
};

Orval Integration

orval.config.ts configures Orval to generate:

    TypeScript interfaces for API requests/responses
    Axios hooks (e.g., useGetUser) for data fetching

To add a new API endpoint:

    Add the endpoint to your OpenAPI spec
    Run pnpm run generate:api
    Use the generated hook in your components:

import { useGetUser } from '@/src/api/generated';

const ProfileScreen = () => {
const { data, isLoading } = useGetUser();
return isLoading ? <Loading /> : <Text>{data.name}</Text>;
};

docs/ai-integration.md
AI Agent Integration
Supported Agents
Agent Configuration File
Claude Code .claude/settings.json, CLAUDE.md
Cursor .cursor/rules/ (add manually)
OpenAI Codex AGENTS.md
Agent Setup

    Claude Code:
        Open the project in Claude Code
        It will automatically read CLAUDE.md and AGENTS.md
    Cursor:
        Create .cursor/rules/app-description.mdc:

        This is an Expo starter template with Orval, Axios, and tab navigation. All source code is in `src/`.

        Create .cursor/rules/app-technologies.mdc:

        Expo SDK, TypeScript, Orval, Axios, Expo Router, React Native

    Codex:
        Start Codex from the project root
        It will read AGENTS.md first

Example Agent Prompts

    "Add a Settings tab: create src/app/settings.tsx, add settings.png to assets/images/tabIcons/, update src/app/_layout.tsx"
    "Generate API types from openapi.json: update orval.config.ts input path, run orval generate"
    "Add a Loading component to src/components/Loading.tsx with a spinning animation"

docs/project-structure.md
Project Structure

Detailed breakdown of all key files/folders from the uploaded zip:
Root Configuration

    app.json: Expo project configuration (app name, icons, splash screen, SDK version)
    orval.config.ts: Orval codegen settings (input spec, output directory, client type)
    tsconfig.json: TypeScript configuration (strict mode, path aliases)
    AGENTS.md: Entry point for all AI agents, lists required read order
    CLAUDE.md: Claude Code-specific rules and prohibitions

Source Code (src/)

    src/config/env.config.ts: Loads and validates environment variables
    src/api/axios.ts: Base Axios instance for all API calls
    src/app/_layout.tsx: Root Expo Router layout with tab navigator
    src/app/index.tsx: Home tab screen (default route)

Assets (assets/)

    assets/images/tabIcons/: Home and Explore tab icons (1x, 2x, 3x resolutions)
    assets/expo.icon/: Adaptive icon set for Android
    assets/images/icon.png: App icon for iOS/Expo Go
    assets/images/splash-icon.png: Splash screen image

AI Instructions

Update existing files and add new folders to enable full AI agent support:
