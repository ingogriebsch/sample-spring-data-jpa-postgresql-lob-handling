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

import static javax.persistence.FetchType.LAZY;

import static lombok.AccessLevel.PACKAGE;

import java.sql.Blob;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Entity
@EqualsAndHashCode(exclude = { "content" })
@Getter
@NoArgsConstructor(access = PACKAGE)
@ToString(exclude = { "content" })
public class Document {

    public Document(@NonNull String id, @NonNull String filename, @NonNull String contentType, @NonNull Long contentLength) {
        this.id = id;
        this.filename = filename;
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    public Document(@NonNull String id, @NonNull String filename, @NonNull String contentType, @NonNull Long contentLength,
        @NonNull Blob content) {
        this(id, filename, contentType, contentLength);
        this.content = content;
    }

    @Id
    private String id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long contentLength;

    @Basic(fetch = LAZY, optional = false)
    @Column(updatable = false, nullable = false)
    @JsonIgnore
    @Lob
    @Setter(PACKAGE)
    private Blob content;

}
