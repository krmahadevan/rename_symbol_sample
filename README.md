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

## Test Case: Rename Custom-Typed Field

**After loading project, invoke:**

```
load_project(projectPath=".")
```

**Then call rename_symbol:**

```
rename_symbol(
  filePath="src/main/java/test/client/ConfigClient.java",
  line=6,           # configManager field (0-indexed, "private ConfigManager configManager;")
  column=25,        # position at start of "configManager"
  newName="configManager_RENAMED"
)
```

**Expected result (with fix):**
- `success=true`
- `totalEdits >= 5` (field def, param, assignment, usages, return)
- `filesAffected >= 1`
- Response includes edits for all references

**Actual result (T16 bug in v1.3.1):**
- `success=true`
- `totalEdits=0`
- `filesAffected=0`
- `editsByFile={}` (empty — no edits found)

## Verifying the Bug

1. Check response JSON:
   ```json
   {
     "success": true,
     "message": "Rename completed",
     "totalEdits": 0,
     "filesAffected": 0,
     "editsByFile": {}
   }
   ```

2. Compare against expected (5+ edits in ConfigClient.java)

3. Root cause: SearchEngine-based approach fails on custom types
   - Finds stdlib types (String, List, etc.) ✓
   - Fails on custom types (ConfigManager) ✗
   - User's fix (PR 3e27560, AST-based approach) would work but isn't in v1.3.1

## Manual Tool Invocation (Without Claude)

### Step 1: Start JavaLens MCP Server

```bash
# Via npm (if installed globally)
npm start @javalens/mcp

# Or via node_modules
npx @javalens/mcp

# Server listens on stdio by default
```

### Step 2: Construct and Send Tool Call

Use MCP protocol to invoke `rename_symbol` directly. Example via stdio/JSON-RPC:

**Request JSON:**
```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "method": "tools/call",
  "params": {
    "name": "rename_symbol",
    "arguments": {
      "filePath": "src/main/java/test/client/ConfigClient.java",
      "line": 6,
      "column": 25,
      "newName": "configManager_RENAMED"
    }
  }
}
```

**Via curl (if server exposes HTTP):**
```bash
curl -X POST http://localhost:3000/mcp/tools/rename_symbol \
  -H "Content-Type: application/json" \
  -d '{
    "filePath": "src/main/java/test/client/ConfigClient.java",
    "line": 6,
    "column": 25,
    "newName": "configManager_RENAMED"
  }'
```

### Step 3: Parse Response

**Success response (with bug — 0 edits):**
```json
{
  "success": true,
  "message": "Rename completed",
  "totalEdits": 0,
  "filesAffected": 0,
  "editsByFile": {}
}
```

**Expected response (if bug fixed):**
```json
{
  "success": true,
  "message": "Rename completed",
  "totalEdits": 5,
  "filesAffected": 1,
  "editsByFile": {
    "src/main/java/test/client/ConfigClient.java": [
      {
        "startLine": 6,
        "startColumn": 25,
        "endLine": 6,
        "endColumn": 40,
        "newText": "configManager_RENAMED"
      },
      {
        "startLine": 8,
        "startColumn": 46,
        "endLine": 8,
        "endColumn": 61,
        "newText": "configManager_RENAMED"
      },
      {
        "startLine": 9,
        "startColumn": 14,
        "endLine": 9,
        "endColumn": 29,
        "newText": "configManager_RENAMED"
      },
      {
        "startLine": 13,
        "startColumn": 25,
        "endLine": 13,
        "endColumn": 40,
        "newText": "configManager_RENAMED"
      },
      {
        "startLine": 23,
        "startColumn": 16,
        "endLine": 23,
        "endColumn": 31,
        "newText": "configManager_RENAMED"
      }
    ]
  }
}
```

### Step 4: Verify Result

- **Bug confirmed**: `totalEdits = 0` despite success=true
- **Root cause**: SearchEngine cannot index custom types (ConfigManager)
- **Expected fix**: AST-based approach would traverse all 5 references

## Why This Reproduces T16

- `ConfigManager` is custom type (not stdlib)
- SearchEngine-based approach fails on non-stdlib custom types
- AST-based approach (user's PR 3e27560) would find all usages
- Demonstrates SearchEngine limitation that v1.3.1 still has
