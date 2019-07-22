/*
 * Copyright 2019 Ingo Griebsch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.ingogriebsch.sample.spring.data.jpa.postgresql.lob.handling;

import static java.nio.file.Files.probeContentType;
import static java.nio.file.Paths.get;
import static java.util.UUID.randomUUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import java.io.InputStream;
import java.sql.Blob;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("integrationtest")
@AutoConfigureTestDatabase(replace = NONE)
@DataJpaTest
@RunWith(SpringRunner.class)
public class DocumentRepositoryTest {

    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private TestEntityManager entityManager;

    @Test(expected = NullPointerException.class)
    public void save_should_fail_if_input_is_null() throws Exception {
        documentRepository.save(null, null);
    }

    @Test
    public void save_should_succeed_if_input_is_given() throws Exception {
        Resource resource = resourceLoader.getResource(ResourceLoader.CLASSPATH_URL_PREFIX + "/documents/simple-svg-file.svg");
        assertThat(resource.exists()).isTrue();

        Document template = new Document(randomUUID().toString(), resource.getFilename(),
            probeContentType(get(resource.getURI())), resource.contentLength());

        Document persisted;
        try (InputStream input = resource.getInputStream()) {
            persisted = documentRepository.save(template, input);
            entityManager.flush();
        }
        entityManager.clear();

        Document found = documentRepository.findOne(persisted.getId());
        assertThat(found).isNotNull();

        Blob blob = found.getContent();
        try {
            assertThat(blob).isNotNull();
            assertThat(blob.getBinaryStream()).isNotNull();
        } finally {
            BlobUtils.free(blob);
        }
    }

    @Test
    public void save_should_store_empty_file() throws Exception {
        Resource resource = resourceLoader.getResource(ResourceLoader.CLASSPATH_URL_PREFIX + "/documents/empty-txt-file.txt");
        assertThat(resource.exists()).isTrue();

        Document template = new Document(randomUUID().toString(), resource.getFilename(),
            probeContentType(get(resource.getURI())), resource.contentLength());

        Document persisted;
        try (InputStream input = resource.getInputStream()) {
            persisted = documentRepository.save(template, input);
            entityManager.flush();
        }
        entityManager.clear();

        Document found = documentRepository.findOne(persisted.getId());
        assertThat(found).isNotNull();

        Blob blob = found.getContent();
        try {
            assertThat(blob).isNotNull();
            assertThat(blob.getBinaryStream()).isNotNull();
        } finally {
            BlobUtils.free(blob);
        }
    }

    @Test(expected = NullPointerException.class)
    public void deleteIfExists_should_throw_exception_if_input_is_null() throws Exception {
        documentRepository.deleteIfExists(null);
    }

    @Test
    public void deleteIfExists_should_return_true_if_document_exists() throws Exception {
        Resource resource = resourceLoader.getResource(ResourceLoader.CLASSPATH_URL_PREFIX + "/documents/simple-svg-file.svg");
        assertThat(resource.exists()).isTrue();

        Document template = new Document(randomUUID().toString(), resource.getFilename(),
            probeContentType(get(resource.getURI())), resource.contentLength());

        Document persisted;
        try (InputStream input = resource.getInputStream()) {
            persisted = documentRepository.save(template, input);
            entityManager.flush();
        }
        entityManager.clear();

        String persistedId = persisted.getId();
        assertThat(documentRepository.exists(persistedId)).isTrue();
        assertThat(documentRepository.deleteIfExists(persistedId)).isTrue();
        assertThat(documentRepository.exists(persistedId)).isFalse();
    }

    @Test
    public void deleteIfExists_should_return_false_if_document_does_not_exist() throws Exception {
        String id = randomUUID().toString();
        assertThat(documentRepository.exists(id)).isFalse();
        assertThat(documentRepository.deleteIfExists(id)).isFalse();
    }
}
