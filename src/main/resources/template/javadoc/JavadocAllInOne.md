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

<% for(api in apiDocList){ %>

## ${htmlEscape(api.desc)}

**Class:** ${api.name}

**Author:** ${api.author}

**Version:** ${api.version}
<% for(doc in api.list){ %>
<%if(doc.deprecated){%>

### ~~${htmlEscape(doc.desc)}~~

<%}else{%>

### ${htmlEscape(doc.desc)}

<%}%>

**Definition:** ${doc.escapeMethodDefinition}

<%if(isNotEmpty(doc.author)){%>
**Author:** ${doc.author}
<%}%>

<%if(isNotEmpty(doc.detail)){%>
**Description:** ${doc.detail}
<%}%>

<%if(isNotEmpty(doc.requestParams)){%>
**Invoke-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
<% for(param in doc.requestParams){ %>
|${param.field}|${htmlEscape(param.type)}|${param.required}|${lineBreaksToBr(param.desc)}|${param.version}|
<%}%>
<%}%>

<%if(isNotEmpty(doc.responseParams)){%>
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
<% for(param in doc.responseParams){ %>
|${param.field}|${htmlEscape(param.type)}|${lineBreaksToBr(param.desc)}|${param.version}|
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
