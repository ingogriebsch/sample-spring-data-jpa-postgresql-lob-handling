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

import static org.springframework.transaction.annotation.Propagation.MANDATORY;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DocumentRepository extends PagingAndSortingRepository<Document, String>, DocumentRepositoryCustom {

    // FIXME Documentation suggests to mark queries which are modifying the database as @Modifying. But using the annotation
    // breaks the call because an executeUpdate (which is called behind the API expects that no result-set is returned (but one is
    // returned because it's a select).
    // @Modifying
    @Query(value = "SELECT lo_unlink(d.content) FROM document d WHERE id = :id", nativeQuery = true)
    @Transactional(propagation = MANDATORY)
    void deleteContent(@Param("id") String id);

}
