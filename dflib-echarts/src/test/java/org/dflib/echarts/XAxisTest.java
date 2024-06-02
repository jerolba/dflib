package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class XAxisTest extends GenerateScriptHtmlTest {

    @Test
    public void data() {

        String s1 = EChart.chart().generateScriptHtml("_tid", df2);
        assertTrue(s1.contains("['labels',1,2,3]"), s1);
        assertTrue(s1.contains("type: 'category'"), s1);

        String s2 = EChart.chart().series("y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("['labels',1,2,3]"), s2);
        assertTrue(s2.contains("type: 'category'"), s2);

        String s3 = EChart.chart().xAxis("x").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("['x','A','B','C']"), s3);
        assertTrue(s3.contains("type: 'category'"), s3);
    }

    @Test
    public void boundaryGap() {

        String s1 = EChart.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("boundaryGap: false,"), s1);

        String s2 = EChart.chart().xAxis("x", XAxis.ofDefault().boundaryGap(false)).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("boundaryGap: false,"), s2);

        String s3 = EChart.chart().xAxis("x", XAxis.ofDefault().boundaryGap(true)).generateScriptHtml("_tid", df1);
        assertFalse(s3.contains("boundaryGap: false,"), s3);
    }

    @Test
    public void name() {

        String s1 = EChart.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("name:"), s1);

        String s2 = EChart.chart().xAxis("x", XAxis.ofDefault().name("I'm an X")).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("name: 'I\\'m an X'"), s2);
    }

    @Test
    public void position() {

        String s1 = EChart.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("position:"), s1);

        String s2 = EChart.chart().xAxis("x", XAxis.ofDefault().top()).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("position: 'top'"), s2);
    }

    @Test
    public void offset() {

        String s1 = EChart.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("offset:"), s1);

        String s2 = EChart.chart().xAxis("x", XAxis.ofDefault().offset(-5)).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("offset: -5"), s2);
    }

    @Test
    public void gridIndex() {

        String s1 = EChart.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("gridIndex:"), s1);

        String s2 = EChart.chart().xAxis("x", XAxis.ofDefault().gridIndex(2)).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("gridIndex: 2,"), s2);
    }

    @Test
    public void alignTicks() {

        String s1 = EChart.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("alignTicks:"), s1);

        String s2 = EChart.chart().xAxis("x", XAxis.ofDefault().alignTicks(true)).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("alignTicks: true,"), s2);

        String s3 = EChart.chart().xAxis("x", XAxis.ofDefault().alignTicks(false)).generateScriptHtml("_tid", df1);
        assertTrue(s3.contains("alignTicks: false,"), s3);
    }

    @Test
    public void axisLine() {

        String s1 = EChart.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("axisLine:"), s1);
        assertFalse(s1.contains("show:"), s1);

        String s2 = EChart.chart().xAxis("x", XAxis.ofDefault().line(AxisLine.of())).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("axisLine: {"), s2);
        assertTrue(s2.contains("show: true"), s2);

        String s3 = EChart.chart().xAxis("x", XAxis.ofDefault().line(AxisLine.of().show(false))).generateScriptHtml("_tid", df1);
        assertTrue(s3.contains("axisLine: {"), s3);
        assertTrue(s3.contains("show: false"), s3);
    }
}
