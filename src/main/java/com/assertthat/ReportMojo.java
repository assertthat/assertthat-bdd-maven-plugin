package com.assertthat;

/**
 * Copyright (c) 2018 AssertThat
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * <p>
 * Created by Glib_Briia on 15/05/2018.
 */

import com.assertthat.plugins.standalone.APIUtil;
import com.assertthat.plugins.standalone.ArgumentsReport;
import com.assertthat.plugins.standalone.FileUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.jettison.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

@Mojo(name = "report", defaultPhase = LifecyclePhase.TEST)
public class ReportMojo extends AbstractMojo {

    @Parameter(property = "accessKey")
    private String accessKey;
    @Parameter(property = "secretKey")
    private String secretKey;
    @Parameter(property = "token")
    private String token;
    @Parameter(property = "projectId", required = true)
    private String projectId;
    @Parameter(property = "runName")
    private String runName;
    @Parameter(property = "jsonReportFolder")
    private String jsonReportFolder;
    @Parameter(property = "jsonReportIncludePattern")
    private String jsonReportIncludePattern;
    @Parameter(property = "proxyURI")
    private String proxyURI;
    @Parameter(property = "proxyUsername")
    private String proxyUsername;
    @Parameter(property = "proxyPassword")
    private String proxyPassword;
    @Parameter(property = "type", defaultValue = "cucumber")
    private String type;
    @Parameter(property = "jiraServerUrl")
    private String jiraServerUrl;
    @Parameter(property = "metadata")
    private String metadata;
    @Parameter(property = "jql")
    private String jql;
    @Parameter(property = "ignoreCertErrors", defaultValue = "false")
    private Boolean ignoreCertErrors;
    @Parameter(property = "runId")
    private Long runId = -1L;
    @Parameter(property = "secretMethod")
    private String secretMethod;
    @Parameter(property = "secretClassName")
    private String secretClassName;

    @Parameter(property = "enabled", defaultValue = "true")
    private Boolean enabled;

    public void execute()
            throws MojoExecutionException {
        if(!enabled) return;
        ArgumentsReport arguments = new ArgumentsReport(
                accessKey,
                secretKey,
                token,
                projectId,
                runName,
                jsonReportFolder,
                jsonReportIncludePattern,
                proxyURI,
                proxyUsername,
                proxyPassword,
                jql,
                type,
                jiraServerUrl,
                metadata,
                ignoreCertErrors
        );
        if(secretClassName!=null) {
            try {
                Class cls;
                Method getSecretKey;
                cls = Class.forName(secretClassName);
                Object util = cls.newInstance();
                getSecretKey = cls.getMethod(secretMethod, String.class);
                arguments.setSecretKey((String) getSecretKey.invoke(util, arguments.getSecretKey()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        APIUtil apiUtil = new APIUtil(arguments.getProjectId(),
                arguments.getAccessKey(),
                arguments.getSecretKey(),
                arguments.getToken(),
                arguments.getProxyURI(),
                arguments.getProxyUsername(),
                arguments.getProxyPassword(),
                arguments.getJiraServerUrl(),
                arguments.isIgnoreCertErrors());
        String[] files = new FileUtil().findJsonFiles(new File(arguments.getJsonReportFolder()), arguments.getJsonReportIncludePattern(), null);
        for (String f : files) {
            try {
                runId = apiUtil.upload(runId, arguments.getRunName(), arguments.getJsonReportFolder() + f, arguments.getType(), arguments.getMetadata(), arguments.getJql());
            } catch (IOException | JSONException e) {
                throw new MojoExecutionException("Failed to upload report", e);
            }
        }
    }

}