
# ${desc}
<%
for(doc in list){
%>
<%if(doc.deprecated){%>
## ~~${htmlEscape(doc.desc)}~~
<%}else{%>
## ${htmlEscape(doc.desc)}
<%}%>

**URL:** `${doc.url}`

**Type:** `${doc.type}`

<%if(isNotEmpty(doc.author)){%>
**Author:** ${doc.author}
<%}%>

**Content-Type:** `${doc.contentType}`

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
