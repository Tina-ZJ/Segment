package com.jz.trie;
import java.util.HashMap;
import java.util.Map;


public class TrieNode
{
	private Character _character=null;
	
	private Double _left_entropy = null;
	
	private Double _right_entropy = null;	
	
	private Double _additional_score = null;
	
	private String _word = null;
	
	private String _tag = null;
	
	private Map<Character, TrieNode> _children;
	
	public Character getCharacter()
	{
		return _character;
	}
	
	public void setCharacter(Character character)
	{
		_character = character;
	}
	
	public Double getLeftEntropy()
	{
		return _left_entropy;
	}
	
	public void setLeftEntropy(Double right_entropy)
	{
		_left_entropy = right_entropy;
	}
	
	public Double getRightEntropy()
	{
		return _right_entropy;
	}
	
	public void setRightEntropy(Double right_entropy)
	{
		_right_entropy = right_entropy;
	}
	
	public Double getRightScore()
	{
		if( _additional_score==null )
		{
			return _right_entropy;
		}
		else
		{
			return _right_entropy+_additional_score;
		}
	}
	
	public Double getLeftScore()
	{
		if( _additional_score==null )
		{
			return _left_entropy;
		}
		else
		{
			return _left_entropy+_additional_score;
		}
	}
	
	public void setAdditionalScore( Double score)
	{
		_additional_score = score;
	}
	
	public Double getAdditionalScore()
	{
		return _additional_score;
	}

	public String getWord()
	{
		return _word;
	}

	public void setWord(String word)
	{
		_word = word;
	}
	
	public String getTag()
	{
		return _tag;
	}

	public void setTag(String tag)
	{
		_tag = tag;
	}
	
	public void addChild(TrieNode node)
	{
		if (_children == null)
		{
			_children = new HashMap<Character, TrieNode>();
		}
		
		if (!_children.containsKey(node.getCharacter()))
		{
			_children.put(node.getCharacter(), node);
		}
	}
	
	public TrieNode getChild(Character ch)
	{
		if (_children == null || !_children.containsKey(ch))
		{
			return null;
		}
		
		return _children.get(ch);
	}
	
	public void removeChild(Character ch)
	{
		if (_children == null || !_children.containsKey(ch))
		{
			return;
		}
		
		_children.remove(ch);
	}
}
