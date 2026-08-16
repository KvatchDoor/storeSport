# 8 Operating Rules


## 🏛️ RULE 0: Clean Architecture is mandatory (CLEAN_ARCHI.md)
All code must respect the hexagonal architecture defined in `/CLEAN_ARCHI.md`:
- ✅ `infrastructure/` → `application/` → `domain/`, never the other way
- ✅ Naming conventions: `*UseCase`, `*Command`, `*Service`, `*Repository`, `*JpaEntity`
- ✅ Ports return domain types, `Optional`, `List` or `boolean` — never a framework type
- ❌ No Spring / JPA / Jackson / SLF4J import in `domain/`
- ❌ No business logic in controllers
- ❌ No database knowledge outside `adapter/out/persistence/`

**Agents:** coder, qa, orchestrator


---


## 🚫 RULE A: Specifications = Inviolable Law
Any explicit specification (numbers, formats, names) is NON-MODIFIABLE without agreement.
- ❌ Use PAGE_SIZE=1000 if asked for 100
- ✅ IF in doubt: ASK FOR CLARIFICATION


**Agents:** coder, qa, orchestrator


---


## 📊 RULE B: Validation of external data
For any external API, request a response example BEFORE coding the DTO.
- ❌ Assume the structure
- ✅ Ask: *"Can you show me an example response?"*


**Agent:** coder


---


## 🏗️ RULE C: Architectural decisions = To be asked
Unspecified choices (PAGE_SIZE, patterns, DTO structures):
- ❌ Decide alone
- ✅ Propose OPTIONS: *"I propose: A, B, C"*
- ✅ WAIT for validation before implementing


**Agent:** coder


---


## 📢 RULE D: Proactive feedback and correction
IF error discovered (untestable code, incoherent structure):
- ❌ Wait for the user to discover it
- ✅ Report immediately: *"I found: ..."*
- ✅ Propose solution


**Agent:** qa


---


## ✅ RULE E: Re-test after refactoring
After refactoring/modification:
1. `mvn clean compile`
2. `get_errors` on modified file
3. `mvn test` (if qa)


**Agents:** coder, qa


---


## ❓ RULE F: Ask before assuming
Facing ambiguity:
- ❌ Code an interpretation
- ✅ Identify the ambiguity
- ✅ Propose 2-3 options
- ✅ WAIT for clarification


**Agents:** coder, qa, orchestrator


---


## 📝 RULE G: Decision traceability
For complex decompositions:
- ✅ Justify why this decomposition
- ✅ Trace who authorizes, when, why
- ✅ List "Open questions" with options in Feature Brief


**Agent:** orchestrator


---


## 🔒 RULE H: One agent, one toolset
Each agent declares its tools in its own `tools:` frontmatter, in `.claude/agents/`. An agent gets
the tools its job needs, and nothing else.

| Tool | coder | qa | orchestrator | reviewer |
|---|:--:|:--:|:--:|:--:|
| `Read` `Glob` `Grep` | ✅ | ✅ | ✅ | ✅ |
| `Edit` `Write` | ✅ | ✅ | ❌ | ❌ |
| `PowerShell` | ✅ | ✅ | ✅ | ✅ |
| `Agent` | ❌ | ❌ | ✅ | ❌ |
| `TodoWrite` | ✅ | ✅ | ✅ | ❌ |

The shell is declared as `PowerShell`, the name this project runs on. Permission rules in
`.claude/settings.local.json` keep their own `Bash(...)` prefix — that is a separate namespace.

Everything absent from the table is denied: `WebFetch`, `WebSearch`, `NotebookEdit`, `Artifact`,
`AskUserQuestion`, and every MCP server. An agent facing an unknown reports it and stops — it does
not go looking for the answer outside the repository.

- ❌ Remove a restriction to unblock a run
- ✅ Report what the missing tool would have been for, and let the user decide

**Restriction granularity is the tool, never the path.** `Edit` given to `qa` is `Edit` on the whole
repository: "qa never touches `src/main/`" and "coder never touches `src/test/`" hold by discipline,
not by the harness.

`reviewer` is declared globally in `~/.claude/agents/`. The copy in `.claude/agents/` overrides it
for this project only, with a read-only toolset.


**Agents:** coder, qa, orchestrator, reviewer


---


## Summary by agent


| Agent | Rules |
|-------|--------|
| **coder** | 0, A, B, C, E, F, H |
| **qa** | 0, A, D, E, F, H |
| **orchestrator** | 0, A, F, G, H |
| **reviewer** | H |



