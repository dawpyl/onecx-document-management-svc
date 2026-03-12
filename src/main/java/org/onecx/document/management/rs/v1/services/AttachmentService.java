package org.onecx.document.management.rs.v1.services;

import java.util.List;
import java.util.Objects;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.onecx.document.management.domain.daos.AttachmentDAO;
import org.onecx.document.management.domain.daos.DocumentDAO;
import org.onecx.document.management.domain.daos.StorageUploadAuditDAO;
import org.onecx.document.management.domain.models.entities.Attachment;
import org.onecx.document.management.rs.v1.exception.RestException;
import org.onecx.document.management.rs.v1.mappers.DocumentMapper;

import gen.org.onecx.document.management.rs.v1.model.AttachmentMetadataUploadDTO;
import gen.org.onecx.document.management.rs.v1.model.AttachmentStorageAuditRequestDTO;

@ApplicationScoped
public class AttachmentService {

    @Inject
    AttachmentDAO attachmentDAO;

    @Inject
    DocumentDAO documentDAO;

    @Inject
    StorageUploadAuditDAO uploadAuditDAO;

    @Inject
    DocumentMapper documentMapper;

    public Attachment getAttachmentDetails(final String attachmentId) {
        final var attachment = attachmentDAO.findById(attachmentId);
        if (Objects.isNull(attachment)) {
            throw new RestException(Response.Status.NOT_FOUND);
        }
        return attachment;
    }

    @Transactional
    public void updateAttachmentsMetadata(List<AttachmentMetadataUploadDTO> attachmentMetadataUploadDTO) {
        for (AttachmentMetadataUploadDTO dto : attachmentMetadataUploadDTO) {
            final var attachmentToUpdate = attachmentDAO.findById(dto.getAttachmentId());
            if (Objects.isNull(attachmentToUpdate)) {
                throw new RestException(Response.Status.NOT_FOUND);
            }
            final var updatedAttachment = documentMapper.updateAttachment(dto, attachmentToUpdate);
            updatedAttachment.setStorageUploadStatus(true);
            attachmentDAO.update(updatedAttachment);
        }
    }

    @Transactional
    public void createStorageAuditLogs(List<AttachmentStorageAuditRequestDTO> requests) {
        for (AttachmentStorageAuditRequestDTO request : requests) {
            final var document = documentDAO.findDocumentById(request.getDocumentId());
            final var attachment = attachmentDAO.findById(request.getAttachmentId());
            if (Objects.isNull(document) || Objects.isNull(attachment)) {
                throw new RestException(Response.Status.NOT_FOUND);
            }
            final var audit = documentMapper.mapToStorageUploadAudit(request.getDocumentId(), document, attachment);
            uploadAuditDAO.create(audit);
        }
    }
}
