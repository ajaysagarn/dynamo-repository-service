package com.ajsa.dyrepo.repository.node.service;

import com.ajsa.dyrepo.repository.content.service.ContentService;
import com.ajsa.dyrepo.repository.property.model.Property;
import com.ajsa.dyrepo.util.RepositoryException;
import com.ajsa.dyrepo.repository.node.dao.NodeDao;
import com.ajsa.dyrepo.repository.node.model.Node;
import com.ajsa.dyrepo.repository.node.model.NodeResponse;
import com.ajsa.dyrepo.util.RandomIdGenerator;
import com.ajsa.dyrepo.util.RepositoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class NodeCrudServiceImpl implements NodeCrudService{

    private final NodeDao nodeDao;

    @Autowired
    public NodeCrudServiceImpl(NodeDao nodeDao) {
        this.nodeDao = nodeDao;
    }

    @Autowired
    public ContentService contentService;

    @Override
    public Node createNode(String parentNodeId, String nodeName, ArrayList<Property> properties) throws RepositoryException {
        try{

            Node node = new Node();

            boolean isNodeId = RepositoryUtils.isNodePathNodeId(parentNodeId);
            Optional<NodeResponse> parentNode = readNode(parentNodeId,0);
            parentNodeId = parentNode.get().getNodeId();

            node.setParentNodeId(parentNodeId);
            if(parentNodeId.contains("-")){
                node.setNodeId(parentNodeId.substring(0,parentNodeId.indexOf("-")).concat("-").concat(RandomIdGenerator.getRandomNodeId()));
            }else{
                node.setNodeId(parentNodeId.concat("-").concat(RandomIdGenerator.getRandomNodeId()));
            }
            node.setNodeType(Node.NodeType.DOCUMENT.toString());
            node.setName(nodeName);
            node.setPath(parentNode.get().getPath().concat("/").concat(node.getName()));
            node.setProperties(properties);

            return nodeDao.createNode(node);

        }catch(RepositoryException e){
            throw e;
        }catch(Exception e){
            throw new RepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error creating a new node");
        }
    }

    @Override
    public Optional<NodeResponse> readNode(String nodeId, Integer levels) throws RepositoryException {
        try{
            Optional<NodeResponse> node =  nodeDao.readNode(nodeId,levels);
            if(node.isEmpty()){
                throw new RepositoryException(HttpStatus.NOT_FOUND.value(), "node does not exist.");
            }
            return node;
        }catch(RepositoryException e){
            throw e;
        }catch (Exception e){
            throw new RepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error reading node");
        }

    }



    @Override
    public Node updateNodeProperties(String nodeId, ArrayList<Property> properties) throws RepositoryException {
        try{
            Node node = readNode(nodeId,0).get();
            node.setProperties(properties);
            return nodeDao.updateNode(node);
        }catch(RepositoryException e){
            throw e;
        }catch (Exception e){
            throw new RepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error updating node");
        }

    }

    @Override
    public Node updateNode(Node n) throws RepositoryException {
        try{
            return nodeDao.updateNode(n);
        } catch (Exception e){
            throw new RepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error updating node");
        }
    }


    @Override
    public void deleteNode(String nodeId, Boolean deleteContent) throws RepositoryException {
        try{
            String nodePath = nodeId;
            if(RepositoryUtils.isNodePathNodeId(nodeId)){
                Node node = readNode(nodeId,0).get();
                nodeId = node.getNodeId();
            }

            if(deleteContent != null && deleteContent){
                contentService.deleteNodeFolder(nodePath);
            }

            nodeDao.deleteNode(nodeId);

        }catch(RepositoryException e){
            throw e;
        }catch (Exception e){
            throw new RepositoryException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error deleting node");
        }
    }

    @Override
    public Node createRepositoryNode() {
        return nodeDao.createRepo();
    }
}
