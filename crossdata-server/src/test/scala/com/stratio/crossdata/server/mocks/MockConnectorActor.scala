/*
 * Licensed to STRATIO (C) under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  The STRATIO (C) licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package com.stratio.crossdata.server.mocks

import akka.actor.{Actor, ActorLogging, Props}
import com.stratio.crossdata.common.logicalplan.LogicalWorkflow
import com.stratio.crossdata.common.result._
import com.stratio.crossdata.communication._
import com.stratio.crossdata.common.result.ErrorType



object State extends Enumeration {
  type state = Value
  val Started, Stopping, Stopped = Value
}

object MockConnectorActor {
  def props(): Props = Props(new MockConnectorActor())
}


class MockConnectorActor() extends Actor with ActorLogging {
  var lastqueryid="wrongqueryid"


  // subscribe to cluster changes, re-subscribe when restart

  override def preStart(): Unit = {
    //#subscribe
    //Cluster(context.system).subscribe(self, classOf[MemberEvent])
    //cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def receive = {
    case (queryid:String,"updatemylastqueryId")=>{
      lastqueryid=queryid
    }
    case lw:LogicalWorkflow=>{
      println(">>>>>>>>>>>>>>>>>>>>>>> lw ")
      val result=QueryResult.createSuccessQueryResult()
      result.setQueryId(lastqueryid)
      sender ! result
    }

    case execute: Execute=> {
      println(">>>>>>>>>>>>>>>>>>>>>>> execute")
      val result=QueryResult.createSuccessQueryResult()
      result.setQueryId( execute.queryId )
      sender ! result
    }

    case storageOp: StorageOperation=> {
      println(">>>>>>>>>>>>>>>>>>>>>>> metadataOp")
      val result=StorageResult.createSuccessFulStorageResult("OK")
      result.setQueryId(storageOp.queryId)
      sender ! result
    }

    case metadataOp: MetadataOperation => {
      println(">>>>>>>>>>>>>>>>>>>>>>> metadataOp2")
      val result=MetadataResult.createSuccessMetadataResult(0)
      val opclass=metadataOp.getClass().toString().split('.')
      opclass( opclass.length -1 ) match{
        case "CreateTable" =>{
          println(">>>>>>>>>>>>>>>>>>>>>>> create table")
          result.setQueryId( metadataOp.asInstanceOf[CreateTable].queryId )
          sender ! result
        }
        case "CreateCatalog"=>{
          println(">>>>>>>>>>>>>>>>>>>>>>> create catalog")
          result.setQueryId( metadataOp.asInstanceOf[CreateCatalog].queryId )
          sender ! result
        }
        case "CreateTableAndCatalog"=>{
          println(">>>>>>>>>>>>>>>>>>>>>>> create table and catalog")
          result.setQueryId( metadataOp.asInstanceOf[CreateTableAndCatalog].queryId )
          sender ! result
        }
        case "CreateIndex"=>{
          result.setQueryId( metadataOp.asInstanceOf[CreateIndex].queryId )
          sender ! result
        }
        case "DropCatalog"=>{
          result.setQueryId( metadataOp.asInstanceOf[DropCatalog].queryId )
          sender ! result
        }
        case "DropIndex"=>{
          result.setQueryId( metadataOp.asInstanceOf[DropIndex].queryId )
          sender ! result
        }
        case "DropTable"=>{
          result.setQueryId( metadataOp.asInstanceOf[DropTable].queryId )
          sender ! result
        }
      }
    }

    case msg: getConnectorName => {
      println(">>>>>>>>>>>>>>>>>>>>>>> connectorName")
      println("getconnectorname")
       sender ! replyConnectorName("myConnector")
     }

    case msg:Object=> {
      val myclass=msg.getClass()
      println(">>>>>>>>>>>>>>>>>>>>>>>other")
      println("non recogniced message from "+sender )
      val err=new ErrorResult(ErrorType.EXECUTION,null)
      sender ! err
    }
    case _=>{
      println(">>>>>>>>>>>>>>>>>>>>>>>other _")
    }

  }
}
