package io.github.corvus400.mockserverbase.catalog

import io.github.corvus400.mockserverbase.plugins.ApiTag

object CatalogHtmlRenderer {
    fun render(entries: List<EndpointEntry>): String {
        val filtered = entries.filter { it.tag != ApiTag.ADMIN && it.tag != ApiTag.SYSTEM }
        val groupedByTag = filtered.sortedBy { it.tag.ordinal }.groupBy { it.tag }
        val groupedByScreen = ScreenMapping.getEndpointsByScreen(filtered)
        val totalEndpoints = filtered.size
        val totalScenarios = filtered.sumOf { it.scenarios.size }

        return buildString {
            append("<!DOCTYPE html>\n")
            append("<html lang=\"ja\">\n")
            append(renderHead())
            append("<body>\n")
            append(renderHeader(totalEndpoints = totalEndpoints, totalScenarios = totalScenarios))
            append(renderCatalogOverview())
            append(renderViewToggle())
            append(renderScreenFilterBar(screens = groupedByScreen.keys))
            append(renderTagFilter(tags = groupedByTag.keys))
            append("<main id=\"main-screen\">\n")
            groupedByScreen.forEach { (screen, screenEntries) ->
                append(renderScreenSection(screen = screen, entries = screenEntries))
            }
            append("</main>\n")
            append("<main id=\"main-api\" style=\"display:none\">\n")
            groupedByTag.forEach { (tag, tagEntries) ->
                append(renderTagSection(tag = tag, entries = tagEntries))
            }
            append("</main>\n")
            append(renderFooter())
            append(renderScript())
            append("</body>\n")
            append("</html>\n")
        }
    }

    private fun renderHead(): String = """
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="theme-color" content="#FAFAF7" media="(prefers-color-scheme: light)">
<meta name="theme-color" content="#12141C" media="(prefers-color-scheme: dark)">
<title>対応画面・シナリオ・Fixture概要カタログ - xxx Mock Server</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=IBM+Plex+Sans:wght@400;500;600;700&family=JetBrains+Mono:wght@400;500;600&display=swap" rel="stylesheet">
<style>
${renderStyles()}
</style>
</head>
"""

    /** テンプレートレンダリングメソッド。引数なしで定数CSSを返す設計 */
    @Suppress("LongMethod", "SameReturnValue")
    private fun renderStyles(): String = """
*,*::before,*::after{box-sizing:border-box;margin:0;padding:0}

:root {
  --font-sans: 'IBM Plex Sans', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  --font-mono: 'JetBrains Mono', 'SF Mono', 'Fira Code', monospace;
  --bg: #FAFAF7;
  --surface: #FFFFFF;
  --surface-hover: #F5F5F0;
  --border: #E0DDD5;
  --border-subtle: #ECEAE3;
  --text: #1A1A1A;
  --text-secondary: #555550;
  --text-muted: #8E8E86;
  --accent: #0D7377;
  --accent-hover: #0A5C5F;
  --accent-subtle: #E6F4F4;
  --tag-bg: #F0EFEA;
  --tag-text: #3D3D38;
  --table-header-bg: #F5F4F0;
  --table-stripe: #FAF9F6;
  --method-get: #0D7377;
  --method-get-bg: #E6F4F4;
  --method-post: #B45309;
  --method-post-bg: #FEF3C7;
  --method-put: #6D28D9;
  --method-put-bg: #F3E8FF;
  --method-delete: #DC2626;
  --method-delete-bg: #FEE2E2;
  --shadow-sm: 0 1px 2px rgba(0,0,0,0.04);
  --shadow-md: 0 2px 8px rgba(0,0,0,0.06);
  --radius: 6px;
  --radius-sm: 4px;
}

@media (prefers-color-scheme: dark) {
  :root {
    color-scheme: dark;
    --bg: #12141C;
    --surface: #1A1D28;
    --surface-hover: #22263A;
    --border: #2E3348;
    --border-subtle: #232738;
    --text: #E8E6E0;
    --text-secondary: #A0A09A;
    --text-muted: #636360;
    --accent: #5BBEC0;
    --accent-hover: #7ED4D6;
    --accent-subtle: #162A2B;
    --tag-bg: #22263A;
    --tag-text: #C8C6C0;
    --table-header-bg: #1E2130;
    --table-stripe: #1C1F2A;
    --method-get-bg: #0D2A2B;
    --method-post-bg: #2A1E06;
    --method-put-bg: #1A102E;
    --method-delete-bg: #2A0A0A;
    --shadow-sm: 0 1px 2px rgba(0,0,0,0.2);
    --shadow-md: 0 2px 8px rgba(0,0,0,0.25);
  }
}

html { font-size: 16px; }
body {
  font-family: var(--font-sans);
  background: var(--bg);
  color: var(--text);
  line-height: 1.6;
  min-height: 100vh;
}

/* Header */
.header {
  background: var(--surface);
  border-bottom: 1px solid var(--border);
  padding: 1.25rem 2rem;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: var(--shadow-sm);
}
.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1.25rem;
  flex-wrap: wrap;
}
.header-title {
  display: flex;
  align-items: baseline;
  gap: 0.6rem;
}
.header-title h1 {
  font-size: 1.5rem;
  font-weight: 700;
  letter-spacing: -0.01em;
  color: var(--text);
  text-wrap: balance;
}
.header-title .subtitle {
  font-size: 0.85rem;
  color: var(--text-muted);
  font-weight: 500;
}
.stats {
  display: flex;
  gap: 1rem;
  font-size: 0.78rem;
  color: var(--text-secondary);
}
.stats .stat-value {
  font-weight: 700;
  font-family: var(--font-mono);
  color: var(--accent);
  font-size: 0.82rem;
  font-variant-numeric: tabular-nums;
}

/* View Toggle — segmented control */
.view-toggle {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0.85rem 2rem 0.6rem;
  display: flex;
  gap: 0;
}
.view-btn {
  font-family: var(--font-sans);
  font-size: 0.82rem;
  font-weight: 600;
  padding: 0.4rem 1rem;
  border: 1px solid var(--border);
  background: var(--surface);
  color: var(--text-secondary);
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s, color 0.15s;
}
.view-btn:first-child {
  border-radius: var(--radius) 0 0 var(--radius);
  border-right: none;
}
.view-btn:last-child {
  border-radius: 0 var(--radius) var(--radius) 0;
}
.view-btn:focus-visible { outline: 2px solid var(--accent); outline-offset: 2px; }
.view-btn:hover {
  background: var(--surface-hover);
  color: var(--accent);
}
.view-btn.active {
  background: var(--accent);
  border-color: var(--accent);
  color: #fff;
}
.view-btn.active + .view-btn {
  border-left-color: var(--accent);
}

/* Search */
.search-bar {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0.85rem 2rem;
}
.search-input {
  width: 100%;
  padding: 0.6rem 1rem 0.6rem 2.4rem;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface);
  color: var(--text);
  font-family: var(--font-sans);
  font-size: 0.88rem;
  transition: border-color 0.15s, box-shadow 0.15s;
}
.search-input::placeholder { color: var(--text-muted); }
.search-input:focus-visible {
  outline: none;
  border-color: var(--accent);
  box-shadow: 0 0 0 3px var(--accent-subtle);
}
.search-wrapper { position: relative; }
.search-icon {
  position: absolute;
  left: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-muted);
  width: 16px;
  height: 16px;
  pointer-events: none;
}

/* Filter Bar (shared by screen and API views) */
.filter-bar {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem 0.75rem;
  display: flex;
  flex-wrap: wrap;
  gap: 0.35rem;
}
.filter-btn {
  font-family: var(--font-sans);
  font-size: 0.78rem;
  font-weight: 500;
  padding: 0.25rem 0.65rem;
  border: 1px solid var(--border);
  border-radius: 100px;
  background: var(--surface);
  color: var(--text-secondary);
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s, color 0.15s;
  white-space: nowrap;
}
.filter-btn:focus-visible { outline: 2px solid var(--accent); outline-offset: 2px; }
.filter-btn:hover {
  background: var(--surface-hover);
  border-color: var(--accent);
  color: var(--accent);
}
.filter-btn.active {
  background: var(--accent);
  border-color: var(--accent);
  color: #fff;
}

/* Main */
main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0.5rem 2rem 3rem;
}

/* Section (shared by screen-section and tag-section) */
.tag-section,
.screen-section { margin-bottom: 1.25rem; }

.tag-header,
.screen-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 0;
  cursor: pointer;
  user-select: none;
  border-bottom: 1px solid var(--border-subtle);
  margin-bottom: 0.6rem;
}
.tag-header:hover .tag-name,
.screen-header:hover .screen-name { color: var(--accent); }
.tag-header:focus-visible,
.screen-header:focus-visible { outline: 2px solid var(--accent); outline-offset: 2px; border-radius: var(--radius-sm); }

.section-chevron {
  width: 14px;
  height: 14px;
  color: var(--text-muted);
  transition: transform 0.2s;
  flex-shrink: 0;
}
.tag-section.collapsed .section-chevron,
.screen-section.collapsed .section-chevron { transform: rotate(-90deg); }
.tag-section.collapsed .tag-content,
.screen-section.collapsed .screen-content { display: none; }

.tag-name,
.screen-name {
  font-size: 1.05rem;
  font-weight: 700;
  transition: color 0.15s;
  text-wrap: balance;
}
.tag-desc,
.screen-desc {
  font-size: 0.82rem;
  color: var(--text-muted);
  font-weight: 400;
}
.section-count {
  font-family: var(--font-mono);
  font-size: 0.73rem;
  background: var(--tag-bg);
  color: var(--tag-text);
  padding: 0.12rem 0.45rem;
  border-radius: 100px;
  margin-left: auto;
}

/* Endpoint Card */
.endpoint {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  margin-bottom: 0.5rem;
  transition: box-shadow 0.15s, border-color 0.15s;
  overflow: hidden;
}
.endpoint:hover { border-color: var(--accent); }
.endpoint-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.7rem 0.85rem;
  cursor: pointer;
  user-select: none;
  flex-wrap: wrap;
}
.endpoint-header:hover { background: var(--surface-hover); }
.endpoint-header:focus-visible { outline: 2px solid var(--accent); outline-offset: -2px; }
.ep-chevron {
  width: 14px;
  height: 14px;
  color: var(--text-muted);
  transition: transform 0.2s;
  flex-shrink: 0;
}
.endpoint.expanded .ep-chevron { transform: rotate(90deg); }
.method-badge {
  font-family: var(--font-mono);
  font-size: 0.72rem;
  font-weight: 600;
  padding: 0.18rem 0.45rem;
  border-radius: var(--radius-sm);
  text-transform: uppercase;
  letter-spacing: 0.03em;
  flex-shrink: 0;
}
.method-GET { background: var(--method-get-bg); color: var(--method-get); }
.method-POST { background: var(--method-post-bg); color: var(--method-post); }
.method-PUT { background: var(--method-put-bg); color: var(--method-put); }
.method-DELETE { background: var(--method-delete-bg); color: var(--method-delete); }
.endpoint-path {
  font-family: var(--font-mono);
  font-size: 0.88rem;
  font-weight: 500;
  color: var(--text);
  word-break: break-all;
}
.endpoint-summary {
  font-size: 0.85rem;
  color: var(--text-secondary);
  margin-left: auto;
  white-space: nowrap;
}
.scenario-count {
  font-family: var(--font-mono);
  font-size: 0.72rem;
  color: var(--text-muted);
  background: var(--tag-bg);
  padding: 0.1rem 0.4rem;
  border-radius: 100px;
  flex-shrink: 0;
}

/* Screen view: swap summary (left) and path (right) */
.screen-section .endpoint-summary {
  order: -1;
  margin-left: 0;
  white-space: normal;
  font-weight: 500;
  color: var(--text);
  font-size: 0.88rem;
}
.screen-section .method-badge {
  order: 2;
  margin-left: auto;
}
.screen-section .endpoint-path {
  order: 3;
  margin-left: 0;
}
.screen-section .scenario-count {
  order: 4;
}

/* Scenario Table */
.scenario-panel {
  display: none;
  border-top: 1px solid var(--border-subtle);
}
.endpoint.expanded .scenario-panel { display: block; }
.scenario-table-wrap {
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}
.scenario-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.85rem;
}
.scenario-table th {
  text-align: left;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--text-muted);
  padding: 0.5rem 0.85rem;
  background: var(--table-header-bg);
  border-bottom: 1px solid var(--border);
  white-space: nowrap;
}
.scenario-table td {
  padding: 0.45rem 0.85rem;
  border-bottom: 1px solid var(--border-subtle);
  vertical-align: top;
}
.scenario-table tr:nth-child(even) td { background: var(--table-stripe); }
.scenario-table tr:last-child td { border-bottom: none; }
.scenario-table .sc-name {
  font-family: var(--font-mono);
  font-size: 0.76rem;
  font-weight: 500;
  color: var(--accent);
  white-space: nowrap;
}
.scenario-table .sc-title {
  font-weight: 500;
  color: var(--text);
  white-space: nowrap;
}
.scenario-table .sc-desc {
  color: var(--text-secondary);
  font-size: 0.78rem;
}

/* Footer */
.footer {
  text-align: center;
  padding: 1.5rem 2rem;
  font-size: 0.73rem;
  color: var(--text-muted);
  border-top: 1px solid var(--border-subtle);
  max-width: 1200px;
  margin: 0 auto;
}
.footer a {
  color: var(--accent);
  text-decoration: none;
}
.footer a:hover { text-decoration: underline; }

/* Catalog Overview */
.catalog-overview {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0.75rem 2rem;
  font-size: 0.85rem;
  line-height: 1.6;
  color: var(--text-secondary);
  border-bottom: 1px solid var(--border);
}
.catalog-overview p { margin: 0 0 0.4rem 0; }
.catalog-overview p:last-child { margin-bottom: 0; }
.catalog-overview strong { color: var(--text); font-weight: 600; }
.catalog-overview a { color: var(--accent); text-decoration: underline; }
.catalog-overview a:hover { text-decoration: none; }

/* No results */
.no-results {
  text-align: center;
  padding: 3rem 1rem;
  color: var(--text-muted);
  font-size: 0.9rem;
  display: none;
}

/* Responsive */
@media (max-width: 768px) {
  .header { padding: 0.85rem 1rem; }
  .header-inner { flex-direction: column; align-items: flex-start; gap: 0.6rem; }
  .catalog-overview { padding: 0.75rem 1rem; }
  .search-bar, main { padding-left: 1rem; padding-right: 1rem; }
  .filter-bar { padding: 0 1rem 0.75rem; }
  .view-toggle { padding: 0.85rem 1rem 0.6rem; }
  .endpoint-header { flex-wrap: wrap; gap: 0.35rem; }
  .endpoint-summary { margin-left: 0; }
  .scenario-table th, .scenario-table td { padding: 0.4rem 0.65rem; }
  .scenario-table .sc-name { font-size: 0.72rem; }
}
"""

    private fun renderHeader(
        totalEndpoints: Int,
        totalScenarios: Int,
    ): String = """
<header class="header">
  <div class="header-inner">
    <div class="header-title">
      <h1>対応画面・シナリオ・Fixture概要カタログ</h1>
      <span class="subtitle">xxx Mock Server</span>
    </div>
    <div class="stats">
      <span><span class="stat-value">$totalEndpoints</span> endpoints</span>
      <span><span class="stat-value">$totalScenarios</span> scenarios</span>
    </div>
  </div>
</header>
<div class="search-bar">
  <div class="search-wrapper">
    <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
      <circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/>
    </svg>
    <input type="text" class="search-input" id="search" placeholder="Search endpoints, scenarios, titles&hellip;" aria-label="Search endpoints" autocomplete="off">
  </div>
</div>
"""

    /** テンプレートレンダリングメソッド。引数なしで定数HTMLを返す設計 */
    @Suppress("SameReturnValue")
    private fun renderCatalogOverview(): String = """
<div class="catalog-overview">
  <p>Mock Server が対応する画面・エンドポイント・シナリオ・Fixture の概要を一覧表示します。</p>
  <p><strong>Swagger UI / ReDoc との違い:</strong> Swagger UI と ReDoc はリクエスト/レスポンスの API 仕様（パラメータ・型・ステータスコード）を提供します。本カタログは「どの画面でどのモックシナリオが利用可能か」「各 Fixture がどのようなデータを返すか」を俯瞰するためのものです。</p>
  <p>テスト計画の策定やシナリオ切替の参照に利用してください。
    <a href="/swagger">Swagger UI</a> ・ <a href="/redoc">ReDoc</a> も併せてご確認ください。</p>
</div>
"""

    /** テンプレートレンダリングメソッド。引数なしで定数HTMLを返す設計 */
    @Suppress("SameReturnValue")
    private fun renderViewToggle(): String = """
<div class="view-toggle">
  <button class="view-btn active" data-view="screen">画面別</button>
  <button class="view-btn" data-view="api">API別</button>
</div>
"""

    private fun renderScreenFilterBar(screens: Set<ScreenTag>): String = buildString {
        append("<div class=\"filter-bar\" id=\"filter-screen\">\n")
        append("  <button class=\"filter-btn active\" data-screen=\"all\">All</button>\n")
        screens.forEach { screen ->
            append(
                "  <button class=\"filter-btn\" data-screen=\"${screen.name}\">" +
                    "${escapeHtml(screen.screenName)}</button>\n",
            )
        }
        append("</div>\n")
    }

    private fun renderTagFilter(tags: Set<ApiTag>): String = buildString {
        append("<div class=\"filter-bar\" id=\"filter-api\" style=\"display:none\">\n")
        append("  <button class=\"filter-btn active\" data-tag=\"all\">All</button>\n")
        tags.forEach { tag ->
            append("  <button class=\"filter-btn\" data-tag=\"${tag.tagName}\">${tag.tagName}</button>\n")
        }
        append("</div>\n")
    }

    private fun renderScreenSection(
        screen: ScreenTag,
        entries: List<EndpointEntry>,
    ): String = buildString {
        append("<section class=\"screen-section\" data-screen=\"${screen.name}\">\n")
        append(
            "  <div class=\"screen-header\" role=\"button\" tabindex=\"0\" " +
                "onclick=\"toggleSection(this)\" onkeydown=\"handleKey(event,this)\">\n",
        )
        append(
            "    <svg class=\"section-chevron\" viewBox=\"0 0 24 24\" fill=\"none\" " +
                "stroke=\"currentColor\" stroke-width=\"2\" aria-hidden=\"true\">" +
                "<path d=\"m6 9 6 6 6-6\"/></svg>\n",
        )
        append("    <span class=\"screen-name\">${escapeHtml(screen.screenName)}</span>\n")
        append("    <span class=\"screen-desc\">${escapeHtml(screen.description)}</span>\n")
        append("    <span class=\"section-count\">${entries.size}</span>\n")
        append("  </div>\n")
        append("  <div class=\"screen-content\">\n")
        entries.forEach { entry -> append(renderEndpointCard(entry = entry)) }
        append("  </div>\n")
        append("</section>\n")
    }

    private fun renderTagSection(
        tag: ApiTag,
        entries: List<EndpointEntry>,
    ): String = buildString {
        append("<section class=\"tag-section\" data-tag=\"${tag.tagName}\">\n")
        append(
            "  <div class=\"tag-header\" role=\"button\" tabindex=\"0\" " +
                "onclick=\"toggleSection(this)\" onkeydown=\"handleKey(event,this)\">\n",
        )
        append(
            "    <svg class=\"section-chevron\" viewBox=\"0 0 24 24\" fill=\"none\" " +
                "stroke=\"currentColor\" stroke-width=\"2\" aria-hidden=\"true\">" +
                "<path d=\"m6 9 6 6 6-6\"/></svg>\n",
        )
        append("    <span class=\"tag-name\">${tag.tagName}</span>\n")
        append("    <span class=\"tag-desc\">${escapeHtml(tag.description)}</span>\n")
        append("    <span class=\"section-count\">${entries.size}</span>\n")
        append("  </div>\n")
        append("  <div class=\"tag-content\">\n")
        entries.forEach { entry -> append(renderEndpointCard(entry = entry)) }
        append("  </div>\n")
        append("</section>\n")
    }

    private fun renderEndpointCard(entry: EndpointEntry): String = buildString {
        val searchData = buildString {
            append("${entry.method.value} ${entry.path} ${entry.summary}")
            entry.scenarios.forEach { sc ->
                append(" ${sc.name} ${sc.title} ${sc.description}")
            }
        }
        append("    <div class=\"endpoint\" data-search=\"${escapeAttr(searchData)}\">\n")
        append(
            "      <div class=\"endpoint-header\" role=\"button\" tabindex=\"0\" " +
                "onclick=\"toggleEndpoint(this)\" onkeydown=\"handleKey(event,this)\">\n",
        )
        append(
            "        <svg class=\"ep-chevron\" viewBox=\"0 0 24 24\" fill=\"none\" " +
                "stroke=\"currentColor\" stroke-width=\"2\" aria-hidden=\"true\">" +
                "<path d=\"m9 18 6-6-6-6\"/></svg>\n",
        )
        append("        <span class=\"method-badge method-${entry.method.value}\">${entry.method.value}</span>\n")
        append("        <span class=\"endpoint-path\">${escapeHtml(entry.path)}</span>\n")
        append("        <span class=\"endpoint-summary\">${escapeHtml(entry.summary)}</span>\n")
        if (entry.scenarios.isNotEmpty()) {
            append("        <span class=\"scenario-count\">${entry.scenarios.size}</span>\n")
        }
        append("      </div>\n")
        if (entry.scenarios.isNotEmpty()) {
            append("      <div class=\"scenario-panel\">\n")
            append("        <div class=\"scenario-table-wrap\">\n")
            append("          <table class=\"scenario-table\">\n")
            append("            <thead><tr>")
            append("<th>Scenario</th><th>Title</th><th>Fixture Description</th>")
            append("</tr></thead>\n")
            append("            <tbody>\n")
            entry.scenarios.forEach { sc ->
                append("              <tr>")
                append("<td class=\"sc-name\">${escapeHtml(sc.name)}</td>")
                append("<td class=\"sc-title\">${escapeHtml(sc.title)}</td>")
                append("<td class=\"sc-desc\">${escapeHtml(sc.description)}</td>")
                append("</tr>\n")
            }
            append("            </tbody>\n")
            append("          </table>\n")
            append("        </div>\n")
            append("      </div>\n")
        }
        append("    </div>\n")
    }

    /** テンプレートレンダリングメソッド。引数なしで定数HTMLを返す設計 */
    @Suppress("SameReturnValue")
    private fun renderFooter(): String = """
<div class="no-results" id="no-results" aria-live="polite">No matching endpoints found.</div>
<footer class="footer">
  <p>Auto-generated from FixtureProvider scenarios &middot;
  <a href="/swagger">Swagger UI</a> &middot;
  <a href="/redoc">ReDoc</a> &middot;
  <a href="/openapi.json">OpenAPI JSON</a></p>
</footer>
"""

    /** テンプレートレンダリングメソッド。引数なしで定数JavaScriptを返す設計 */
    @Suppress("LongMethod", "SameReturnValue")
    private fun renderScript(): String = """
<script>
(function(){
  var search = document.getElementById('search');
  var noResults = document.getElementById('no-results');
  var mainScreen = document.getElementById('main-screen');
  var mainApi = document.getElementById('main-api');
  var filterScreen = document.getElementById('filter-screen');
  var filterApi = document.getElementById('filter-api');
  var viewBtns = document.querySelectorAll('.view-btn');
  var activeView = 'screen';
  var activeScreenTag = 'all';
  var activeApiTag = 'all';

  viewBtns.forEach(function(btn) {
    btn.addEventListener('click', function() {
      viewBtns.forEach(function(b) { b.classList.remove('active'); });
      btn.classList.add('active');
      activeView = btn.getAttribute('data-view');
      if (activeView === 'screen') {
        mainScreen.style.display = '';
        mainApi.style.display = 'none';
        filterScreen.style.display = '';
        filterApi.style.display = 'none';
      } else {
        mainScreen.style.display = 'none';
        mainApi.style.display = '';
        filterScreen.style.display = 'none';
        filterApi.style.display = '';
      }
      applyFilters();
    });
  });

  filterScreen.querySelectorAll('.filter-btn').forEach(function(btn) {
    btn.addEventListener('click', function() {
      filterScreen.querySelectorAll('.filter-btn').forEach(function(b) { b.classList.remove('active'); });
      btn.classList.add('active');
      activeScreenTag = btn.getAttribute('data-screen');
      applyFilters();
    });
  });

  filterApi.querySelectorAll('.filter-btn').forEach(function(btn) {
    btn.addEventListener('click', function() {
      filterApi.querySelectorAll('.filter-btn').forEach(function(b) { b.classList.remove('active'); });
      btn.classList.add('active');
      activeApiTag = btn.getAttribute('data-tag');
      applyFilters();
    });
  });

  search.addEventListener('input', function() { applyFilters(); });

  function applyFilters() {
    var q = search.value.toLowerCase();
    var visibleCount = 0;

    if (activeView === 'screen') {
      mainScreen.querySelectorAll('.screen-section').forEach(function(sec) {
        var screen = sec.getAttribute('data-screen');
        var screenMatch = activeScreenTag === 'all' || screen === activeScreenTag;
        if (!screenMatch) { sec.style.display = 'none'; return; }
        sec.style.display = '';
        var cards = sec.querySelectorAll('.endpoint');
        var sectionVisible = 0;
        cards.forEach(function(card) {
          var text = card.getAttribute('data-search').toLowerCase();
          var match = !q || text.indexOf(q) !== -1;
          card.style.display = match ? '' : 'none';
          if (match) sectionVisible++;
        });
        sec.style.display = sectionVisible > 0 ? '' : 'none';
        visibleCount += sectionVisible;
      });
    } else {
      mainApi.querySelectorAll('.tag-section').forEach(function(sec) {
        var tag = sec.getAttribute('data-tag');
        var tagMatch = activeApiTag === 'all' || tag === activeApiTag;
        if (!tagMatch) { sec.style.display = 'none'; return; }
        sec.style.display = '';
        var cards = sec.querySelectorAll('.endpoint');
        var sectionVisible = 0;
        cards.forEach(function(card) {
          var text = card.getAttribute('data-search').toLowerCase();
          var match = !q || text.indexOf(q) !== -1;
          card.style.display = match ? '' : 'none';
          if (match) sectionVisible++;
        });
        sec.style.display = sectionVisible > 0 ? '' : 'none';
        visibleCount += sectionVisible;
      });
    }

    noResults.style.display = visibleCount === 0 ? 'block' : 'none';
  }
})();

function toggleSection(header) {
  header.parentElement.classList.toggle('collapsed');
}

function toggleEndpoint(header) {
  header.parentElement.classList.toggle('expanded');
}

function handleKey(e, el) {
  if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); el.click(); }
}
</script>
"""

    private fun escapeHtml(text: String): String =
        text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")

    private fun escapeAttr(text: String): String =
        text.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;")
}
