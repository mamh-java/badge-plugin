/*
 * The MIT License
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., Serban Iordache
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.jenkinsci.plugins.badge.dsl;

import com.jenkinsci.plugins.badge.action.HtmlBadgeAction;
import hudson.model.BuildBadgeAction;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class AddHtmlBadgeStepTest extends AbstractBadgeTest {

  @Test
  public void addHtmlBadge() throws Exception {
    String html = UUID.randomUUID().toString();
    testAddHtmlBadge(html, html);
  }

  @Test
  public void addHtmlBadge_remove_script() throws Exception {
    String uuid = UUID.randomUUID().toString();
    String html = uuid + "<script>alert('exploit!');</script>";
    testAddHtmlBadge(html, uuid);
  }


  private void testAddHtmlBadge(String html, String expected) throws Exception {
    WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "p");

    String script = "addHtmlBadge(\"" + html + "\")";

    p.setDefinition(new CpsFlowDefinition(script, true));
    WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));

    List<BuildBadgeAction> badgeActions = b.getBadgeActions();
    assertEquals(1, badgeActions.size());

    HtmlBadgeAction action = (HtmlBadgeAction) badgeActions.get(0);
    assertEquals(expected, action.getHtml());
    assertEquals(html, action.getRawHtml());
  }
}
