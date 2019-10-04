package com.jz.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LongestPathDAG
{
    public static class Graph
    {
        private int _vex_num;
        
        private double[][] _weight_matrix;
        
        private double[] _max_distances;
        
        private MyStack _topological_stack;
        
        private boolean[] _visited;
        
        private int[] _longest_path_predecessor;

        public Graph(int vex_num)
        {
        	_vex_num = vex_num;
            _weight_matrix = new double[_vex_num][_vex_num];
            
            _visited = new boolean[_vex_num];
            _longest_path_predecessor = new int[_vex_num];
            _max_distances = new double[_vex_num];

            _topological_stack = new MyStack(_vex_num);
            
            for (int i = 0; i < _vex_num; i++)
            {
            	_max_distances[i] = Integer.MIN_VALUE;
            	_longest_path_predecessor[i] = -1;
            }
        }

        public void addEdge(int begin, int end, double weight)
        {
        	_weight_matrix[begin][end] = weight;
        }
        
        public List<Integer> findLongestPath()
        {
        	topologicalSort();
        	
        	_max_distances[0] = 0;

        	dynamicSearch();
           
            List<Integer> path = new ArrayList<Integer>();
            
            path.add(_vex_num-1);
            
            int pre = _longest_path_predecessor[_vex_num-1];

            while (pre != 0)
            {
            	path.add(pre);
            	pre = _longest_path_predecessor[pre];
            }
            
            path.add(0);
            
            Collections.reverse(path);
            
            return path;
        }
  
        private void dynamicSearch()
        {
            while (!_topological_stack.isEmpty())
            {
                int vex = _topological_stack.pop();
                
                if (_max_distances[vex] != Integer.MIN_VALUE)
                {
                    for (int adjacent = 0; adjacent < _vex_num; adjacent++)
                    {
                        if (_weight_matrix[vex][adjacent] != 0)
                        {
                            if (_max_distances[adjacent] < _max_distances[vex] + _weight_matrix[vex][adjacent])
                            {
                            	_longest_path_predecessor[adjacent] = vex;
                            	
                                _max_distances[adjacent] = _max_distances[vex] + _weight_matrix[vex][adjacent];
                            }
                        }
                    }
                }
            }
        }

        private void topologicalSort()
        {
            for (int vex = 0; vex < _vex_num; vex++)
            {
                if (!_visited[vex])
                {
                	DFS(vex);
                }
            }
        }

        //gkm: deep first search
        private void DFS(int vex)
        {
        	_visited[vex] = true;
            
            for (int adjacent = 0; adjacent < _vex_num; adjacent++)
            {
                if (_weight_matrix[vex][adjacent] != 0 && !_visited[adjacent])
                {
                	DFS(adjacent);
                }
            }
                        
            _topological_stack.push(vex);
        }

    }

    public static class MyStack
    {
        private int[] stack;
        private int top = -1;
        private int size = 0;

        public MyStack(int maxSize)
        {
            stack = new int[maxSize];
        }

        public void push(int item)
        {
            stack[++top] = item;
            size++;
        }

        public int pop()
        {
            int item = stack[top--];
            size--;
            return item;
        }

        public boolean isEmpty()
        {
            return size == 0;
        }
    }
    
    public static void main(String[] args)
    {
        Graph g = new Graph(6);
        g.addEdge(0, 1, 5.1);
        g.addEdge(0, 2, 3.2);
        g.addEdge(1, 3, 6.3);
        g.addEdge(1, 2, 2.4);
        g.addEdge(2, 4, 4.5);
        g.addEdge(2, 5, 2.6);
        g.addEdge(2, 3, 7.7);
        g.addEdge(3, 5, 1.8);
        g.addEdge(3, 4, -1.0);
        g.addEdge(4, 5, -2.0);

        System.out.println( g.findLongestPath() );
    }

}