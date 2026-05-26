# Rename Symbol Sample — T16 Reproduction

Minimal project to reproduce JavaLens rename_symbol bug (T16) where custom-typed field rename returns 0 edits.

## Prerequisites

- Java 17+
- Maven 3.6+
- JavaLens MCP server running (v1.3.1 or later)
- Claude Code or compatible MCP client

## Local Setup

```bash
cd /Users/krishnanmahadevan/githome/lab/java_lens_playground/rename_symbol_sample
mvn clean compile
```

## Invoking JavaLens MCP Server

### Option 1: Use Claude Code (Recommended)

1. **Start Claude Code in this directory:**
   ```bash
   cd /Users/krishnanmahadevan/githome/lab/java_lens_playground/rename_symbol_sample
   claude code  # or use IDE extension
   ```

2. **JavaLens MCP server auto-connects** (configured in settings)

3. **Load the project in Claude Code:**
   - Type in prompt: `load_project(projectPath=".")`
   - Wait for "Project loaded" confirmation

### Option 2: Test via MCP directly (if running standalone server)

```bash
# Ensure JavaLens MCP server is running on your configured port
# Then send tool call to rename_symbol
```
