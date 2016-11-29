package com.traumkern.mediaregistry.test.mock;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class FirstArgumentReturnedAnswer<T> implements Answer<T> {

    @Override
    public T answer(final InvocationOnMock argInvocation) throws Throwable {
        return (T) argInvocation.getArguments()[0];
    }

}
