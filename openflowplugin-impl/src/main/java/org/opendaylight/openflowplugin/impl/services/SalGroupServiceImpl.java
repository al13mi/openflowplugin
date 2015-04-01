/**
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowplugin.impl.services;

import org.opendaylight.openflowplugin.api.openflow.device.Xid;

import org.opendaylight.yangtools.yang.binding.DataObject;
import com.google.common.base.Function;
import org.opendaylight.openflowplugin.openflow.md.core.sal.convertor.GroupConvertor;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.service.rev130918.AddGroupInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.service.rev130918.AddGroupOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.service.rev130918.RemoveGroupInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.service.rev130918.RemoveGroupOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.service.rev130918.SalGroupService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.service.rev130918.UpdateGroupInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.service.rev130918.UpdateGroupOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.types.rev131018.Group;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GroupModInputBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import java.util.concurrent.Future;

public class SalGroupServiceImpl extends CommonService implements SalGroupService {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(SalGroupServiceImpl.class);

    @Override
    public Future<RpcResult<AddGroupOutput>> addGroup(final AddGroupInput input) {
        return this.<AddGroupOutput, Void> handleServiceCall( PRIMARY_CONNECTION,
                 new Function<DataCrate<AddGroupOutput>,Future<RpcResult<Void>>>() {

                    @Override
                    public Future<RpcResult<Void>> apply(final DataCrate<AddGroupOutput> data) {
                        return convertAndSend(input, data);
                    }
                });
    }

    @Override
    public Future<RpcResult<UpdateGroupOutput>> updateGroup(final UpdateGroupInput input) {
        return this.<UpdateGroupOutput, Void> handleServiceCall(PRIMARY_CONNECTION,
                new Function<DataCrate<UpdateGroupOutput>, Future<RpcResult<Void>>>() {

                    @Override
                    public Future<RpcResult<Void>> apply(final DataCrate<UpdateGroupOutput> data) {
                        return convertAndSend(input.getUpdatedGroup(), data);
                    }
                });
    }

    @Override
    public Future<RpcResult<RemoveGroupOutput>> removeGroup(final RemoveGroupInput input) {
        return this.<RemoveGroupOutput, Void> handleServiceCall(PRIMARY_CONNECTION,
                new Function<DataCrate<RemoveGroupOutput>, Future<RpcResult<Void>>>() {

                    @Override
                    public Future<RpcResult<Void>> apply(final DataCrate<RemoveGroupOutput> data) {
                        return convertAndSend(input, data);
                    }
                });
    }

    <T extends DataObject> Future<RpcResult<Void>> convertAndSend(final Group iputGroup, final DataCrate<T> data) {
        final GroupModInputBuilder ofGroupModInput = GroupConvertor.toGroupModInput(iputGroup, version, datapathId);
        final Xid xid = deviceContext.getNextXid();
        ofGroupModInput.setXid(xid.getValue());
        data.getRequestContext().setXid(xid);
        return provideConnectionAdapter(data.getiDConnection()).groupMod(ofGroupModInput.build());
    }
}