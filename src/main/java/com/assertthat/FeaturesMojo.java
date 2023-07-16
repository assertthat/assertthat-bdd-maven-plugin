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
import com.assertthat.plugins.standalone.ArgumentsFeatures;
import com.assertthat.plugins.standalone.FileUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

@Mojo(name = "features", defaultPhase = LifecyclePhase.GENERATE_TEST_RESOURCES)
public class FeaturesMojo extends AbstractMojo {

    @Parameter(property = "accessKey")
    private String accessKey;

    @Parameter(property = "secretKey")
    private String secretKey;

    @Parameter(property = "projectId", required = true)
    private String projectId;
    @Parameter(property = "tags")
    private String tags;
    @Parameter(property = "outputFolder")
    private String outputFolder;
    @Parameter(property = "proxyURI")
    private String proxyURI;
    @Parameter(property = "proxyUsername")
    private String proxyUsername;
    @Parameter(property = "proxyPassword")
    private String proxyPassword;
    @Parameter(property = "mode", defaultValue = "automated")
    private String mode;
    @Parameter(property = "jql")
    private String jql;
    @Parameter(property = "jiraServerUrl")
    private String jiraServerUrl;
    @Parameter(property = "numbered", defaultValue = "true")
    private Boolean numbered;
    @Parameter(property = "ignoreCertErrors", defaultValue = "false")
    private Boolean ignoreCertErrors;
    @Parameter(property = "secretMethod")
    private String secretMethod;
    @Parameter(property = "secretClassName")
    private String secretClassName;

    @Parameter(property = "enabled", defaultValue = "true")
    private Boolean enabled;
    public void execute()
            throws MojoExecutionException {
        if(!enabled) return;
        ArgumentsFeatures arguments = new ArgumentsFeatures(
                accessKey,
                secretKey,
                projectId,
                outputFolder,
                proxyURI,
                proxyUsername,
                proxyPassword,
                mode,
                jql,
                tags,
                jiraServerUrl,
                numbered,
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
                arguments.getProxyURI(),
                arguments.getProxyUsername(),
                arguments.getProxyPassword(),
                arguments.getJiraServerUrl(),
                arguments.isIgnoreCertErrors());

        try {
            File inZip =
                    apiUtil.download(new File(arguments.getOutputFolder()),
                            arguments.getMode(), arguments.getJql(), arguments.getTags(), arguments.isNumbered());
            File zip = new FileUtil().unpackArchive(inZip, new File(arguments.getOutputFolder()));
            zip.delete();
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to download features", e);
        }

    }
}