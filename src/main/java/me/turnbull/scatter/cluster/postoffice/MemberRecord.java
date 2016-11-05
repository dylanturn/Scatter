/*
 * Scatter - Distributed Processing Platform
 * Copyright (C) 2016 Dylan Turnbull [dylanturn@gmail.com]
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package me.turnbull.scatter.cluster.postoffice;

import org.jgroups.Address;

/**
 * Package: me.turnbull.scatter.cluster.postoffice
 * Date:    11/4/2016
 * Author:  Dylan
 */
public class MemberRecord {
    public final Address memberAddress;
    public final String memberIpAddress;
    public final long memberPing;
    public MemberRecord(Address memberAddress,String memberIpAddress, long memberPing){
        this.memberAddress = memberAddress;
        this.memberIpAddress = memberIpAddress;
        this.memberPing = memberPing;
    }
}
