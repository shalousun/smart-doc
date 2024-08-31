<%if(isNotEmpty(projectName)){%>
# ${projectName}
<%}%>

<%if(isNotEmpty(revisionLogList)){%>

| Version | Update Time | Status | Author | Description |
|---------|-------------|--------|--------|-------------|
<% for(revisionLog in revisionLogList){ %>
|${revisionLog.version} |${revisionLog.revisionTime} |${revisionLog.status} |${revisionLog.author} |${lineBreaksToBr(revisionLog.remarks)}|
<%}%>

<%}%>

<%
for(doc in webSocketDocList){
%>
<%if(doc.deprecated){%>
## ~~${htmlEscape(doc.desc)}~~
<%}else{%>
## ${htmlEscape(doc.desc)}
<%}%>
**URI:** ${doc.uri}

**Author:** ${doc.author}

**Description:** ${doc.desc}

**SubProtocols:** ${doc.subProtocols}

<%if(isNotEmpty(doc.pathParams)){%>

**Path-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
<%
for(param in doc.pathParams){
%>
|${param.field}|${param.type}|${param.required}|${lineBreaksToBr(param.desc)}|${param.version}|
<%}%>
<%}%>

<%if(isNotEmpty(doc.messageParams)){%>

**Message-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
<%
for(param in doc.messageParams){
%>
|${param.field}|${param.type}|${param.required}|${lineBreaksToBr(param.desc)}|${param.version}|
<%}%>
<%}%>

<%if(isNotEmpty(doc.responseParams)){%>

**Response-parameters:**
<%
for(param in doc.responseParams){
%>

| Parameter | Type | Description | Since |
|-----------|------|-------------|-------|
<%
for(paramItem in param){
%>
|${paramItem.field}|${paramItem.type}|${lineBreaksToBr(paramItem.desc)}|${paramItem.version}|
<%}%>
<%}%>
<%}%>

<%}%>
<%if(isNotEmpty(errorCodeList)){%>

## ${errorListTitle}

| Error code | Description |
|------------|-------------|
<% for(error in errorCodeList){ %>
|${error.value}|${htmlEscape(error.desc)}|
<%}%>

<%}%>

<%if(isNotEmpty(dictList)){%>

## ${dictListTitle}

<% for(dict in dictList){ %>

### ${dict.title}

| Name | Code | Type | Description |
|------|------|------|-------------|
<% for(dataDict in dict.dataDictList){ %>
|${dataDict.name}|${dataDict.value}|${dataDict.type}|${htmlEscape(dataDict.desc)}|
<%}%>

<%}%>
<%}%>
