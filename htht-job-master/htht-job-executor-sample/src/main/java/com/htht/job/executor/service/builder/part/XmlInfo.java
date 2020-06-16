package com.htht.job.executor.service.builder.part;

public class XmlInfo {
    String inputxml;
    String outputxml;

    public XmlInfo(String inputxml, String outputxml) {
        super();
        this.inputxml = inputxml;
        this.outputxml = outputxml;
    }

    public String getInputxml() {
        return inputxml;
    }

    public void setInputxml(String inputxml) {
        this.inputxml = inputxml;
    }

    public String getOutputxml() {
        return outputxml;
    }

    public void setOutputxml(String outputxml) {
        this.outputxml = outputxml;
    }
}
