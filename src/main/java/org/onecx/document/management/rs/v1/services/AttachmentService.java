package org.onecx.document.management.rs.v1.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.onecx.document.management.domain.daos.AttachmentDAO;
import org.onecx.document.management.domain.models.entities.Attachment;
import org.onecx.document.management.rs.v1.exception.RestException;

import java.util.Objects;

@ApplicationScoped
public class AttachmentService {

    @Inject
    AttachmentDAO attachmentDAO;

    public Attachment getAttachmentDetails(final String attachmentId) {
        final var attachment = attachmentDAO.findById(attachmentId);
        if (Objects.isNull(attachment)) {
            throw new RestException(Response.Status.NOT_FOUND);
        }
        return attachment;
    }
}
