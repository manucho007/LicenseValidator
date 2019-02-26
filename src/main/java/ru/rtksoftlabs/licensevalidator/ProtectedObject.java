package ru.rtksoftlabs.licensevalidator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ProtectedObject {
    private List<String> listOfStringsWithPathToAllLeafs;

    public String data;

    private ProtectedObject parent;

    public List<ProtectedObject> children;

    public ProtectedObject() {
    }

    public ProtectedObject(String data) {
        this.data = data;
        this.children = new LinkedList<>();
    }

    public ProtectedObject addChild(String child) {
        ProtectedObject childNode = new ProtectedObject(child);
        childNode.parent = this;
        this.children.add(childNode);
        return childNode;
    }

    public List<String> generateListOfAllPathsToLeafs(ProtectedObject node, String accumulator) {
        listOfStringsWithPathToAllLeafs = new ArrayList<>();

        for (ProtectedObject child: node.children) {
            String elem = accumulator;
            elem += "/" + child.data;

            if ((child.children != null) && (child.children.size() > 0)) {
                generateListOfAllPathsToLeafs(child, elem);
            } else {
                listOfStringsWithPathToAllLeafs.add(elem);
            }
        }

        return listOfStringsWithPathToAllLeafs;
    }

    public List<String> getListOfStringsWithPathToAllLeafs() {
        return listOfStringsWithPathToAllLeafs;
    }

    public boolean find(ProtectedObject protectedObject) {
        if(listOfStringsWithPathToAllLeafs == null) {
            generateListOfAllPathsToLeafs(this, data);
        }

        if (protectedObject.getListOfStringsWithPathToAllLeafs() == null) {
            protectedObject.generateListOfAllPathsToLeafs(protectedObject, protectedObject.data);
        }

        List<String> otherList = protectedObject.getListOfStringsWithPathToAllLeafs();

        return listOfStringsWithPathToAllLeafs.containsAll(otherList);
    }
}
