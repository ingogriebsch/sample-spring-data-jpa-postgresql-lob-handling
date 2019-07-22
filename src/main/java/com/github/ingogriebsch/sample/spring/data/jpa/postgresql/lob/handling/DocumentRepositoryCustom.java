package com.github.ingogriebsch.sample.spring.data.jpa.postgresql.lob.handling;

import java.io.InputStream;

public interface DocumentRepositoryCustom {

    Document save(Document template, InputStream source) throws Exception;

    boolean deleteIfExists(String id);

}
