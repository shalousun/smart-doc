<%if(isNotEmpty(projectName)){%>
# ${projectName}
<%}%>
<%if(isNotEmpty(revisionLogList)){%>

| Version | Update Time | Status | Author | Description |
|---------|-------------|--------|--------|-------------|
<%
for(revisionLog in revisionLogList){
%>
|${revisionLog.version}|${revisionLog.revisionTime}|${revisionLog.status}|${revisionLog.author}|${revisionLog.remarks}|
<%}%>

<%}%>


<%
for(apiGroup in apiDocList){
%>
<%
if(!apiDocListOnlyHasDefaultGroup) {%>
# ${apiGroup.group}
<%}%>
<%
for(api in apiGroup.childrenApiDocs){
%>
## ${api.desc}
<%
for(doc in api.list){
%>
<%if(doc.deprecated){%>
### ~~${htmlEscape(doc.desc)}~~
<%}else{%>
### ${htmlEscape(doc.desc)}
<%}%>
**URL:** ${doc.url}

**Type:** ${doc.type}

<%if(isNotEmpty(doc.author)){%>
**Author:** ${doc.author}
<%}%>

**Content-Type:** ${doc.contentType}

<%if(isNotEmpty(doc.detail)){%>
**Description:** ${doc.detail}
<%}%>
<%if(isNotEmpty(doc.requestHeaders)){%>

**Request-headers:**

| Header | Type | Required | Description | Since | Example |
|--------|------|----------|-------------|-------|---------|
<%
for(param in doc.requestHeaders){
%>
|${param.name}|${param.type}|${param.required}|${lineBreaksToBr(param.desc)}|${param.since}|${param.value}|
<%}%>
<%}%>

<%if(isNotEmpty(doc.pathParams)){%>

**Path-parameters:**

| Parameter | Type | Required | Description | Since | Example |
|-----------|------|----------|-------------|-------|---------|
<%
for(param in doc.pathParams){
%>
|${param.field}|${param.type}|${param.required}|${lineBreaksToBr(param.desc)}|${param.version}|${param.value}|
<%}%>
<%}%>
<%if(isNotEmpty(doc.queryParams)){%>

**Query-parameters:**

| Parameter | Type | Required | Description | Since | Example |
|-----------|------|----------|-------------|-------|---------|
<%
for(param in doc.queryParams){
%>
|${param.field}|${param.type}|${param.required}|${lineBreaksToBr(param.desc)}|${param.version}|${param.value}|
<%}%>
<%}%>
<%if(isNotEmpty(doc.requestParams)){%>

**Body-parameters:**

| Parameter | Type | Required | Description | Since | Example |
|-----------|------|----------|-------------|-------|---------|
<%
for(param in doc.requestParams){
%>
|${param.field}|${param.type}|${param.required}|${lineBreaksToBr(param.desc)}|${param.version}|${param.value}|
<%}%>
<%}%>

<%if(isNotEmpty(doc.requestUsage)&&isRequestExample){%>
**Request-example:**
```bash
${doc.requestUsage}
```
<%}%>
<%if(isNotEmpty(doc.responseParams)){%>
**Response-fields:**

| Field | Type | Description | Since | Example |
|-------|------|-------------|-------|---------|
<%
for(param in doc.responseParams){
%>
|${param.field}|${param.type}|${lineBreaksToBr(param.desc)}|${param.version}|${param.value}|
<%}%>
<%}%>

<%if(isNotEmpty(doc.responseUsage)&&isResponseExample){%>
**Response-example:**
```json
${doc.responseUsage}
```
<%}%>

<% } %>
<% } %>
<% } %>
<%if(isNotEmpty(errorCodeList)){%>
<%if(apiDocListOnlyHasDefaultGroup) { %>
## ${errorListTitle}
<% } else { %>
# ${errorListTitle}
<% } %>

| Error code | Description |
|------------|-------------|
<%
for(error in errorCodeList){
%>
|${error.value}|${htmlEscape(error.desc)}|
<%}%>
<%}%>

<%if(isNotEmpty(dictList)){%>
<%if(apiDocListOnlyHasDefaultGroup) { %>
## ${dictListTitle}
<% } else { %>
# ${dictListTitle}
<% } %>

<%
for(dict in dictList){
%>

<%if(apiDocListOnlyHasDefaultGroup) { %>
### ${dict.title}
<% } else { %>
## ${dict.title}
<% } %>

| Name | Code | Type | Description |
|------|------|------|-------------|
<%
for(dataDict in dict.dataDictList){
%>
|${dataDict.name}|${dataDict.value}|${dataDict.type}|${htmlEscape(dataDict.desc)}|
<%}%>
<%}%>
<%}%>
