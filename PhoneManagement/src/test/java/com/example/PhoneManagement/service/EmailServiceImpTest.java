package com.example.PhoneManagement.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

class EmailServiceImpTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImp emailServiceImp;

    public EmailServiceImpTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendEmailSuccess() throws MessagingException {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String content = "Test Content";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);


        emailServiceImp.sendEmail(to, subject, content);


        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendEmailFailure() throws MessagingException {

        String to = "test@example.com";
        String subject = "Test Subject";
        String content = "Test Content";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);


        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                throw new MessagingException("Failed to send email");
            }
        }).when(mailSender).send(mimeMessage);


        emailServiceImp.sendEmail(to, subject, content);


        verify(mailSender, times(1)).send(mimeMessage);
    }
}
