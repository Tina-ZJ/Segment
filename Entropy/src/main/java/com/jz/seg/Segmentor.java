package com.jz.seg;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.jz.utility.MyPair;
import com.jz.trie.TrieNode;
import com.jz.utility.MyCharacter;
import com.jz.utility.MyLetter;
import com.jz.utility.MyPair;

public class Segmentor
{
	public static final String SUFFIX = "机|器|壳|枪|套|装|球|床|膜|仪|衫|鞋|靴|箱|笔|裤|油|服|包|袜|垫|网|架|笔|芯|拍|帽|锁|杆|车|衣|盒|罩|灯|饼|茶|膏|店|浆|片|券|锅|味|型|版|色|式|款|牌";
	public static final String UNIT = "p|匹|kg|千克|克拉|公斤|斤|克|g|盎司|oz|磅|l|升|ml|毫升|mah|毫安时|毫米|mm|厘米|cm|分米|dm|m|米|寸|英尺|ft|英寸|吋|岁|年|月|日|分|秒|克拉|万|元|角|分钟|片|t";
//	public static final String UNIT = "p|kg|g|oz|l|ml|mah|mm|cm|dm|m|ft|t";

	private static final double CONST = 0.1;
	private static final String TAG = "O";
	
	private static HashSet<String> _suffix_set=null;
	private static HashSet<String> _unit_set=null;
	
	private TrieNode _trie = null;
	private String path = System.getProperty("user.dir");



	private volatile static Segmentor _singleton = null;

	private Segmentor() throws IOException
	{
		String [] arr = SUFFIX.split("\\|");
		
		_suffix_set=new HashSet<String>();
		
		for(String unit : arr)
		{
			_suffix_set.add(unit);
		}
		
		arr = UNIT.split("\\|");
		
		_unit_set=new HashSet<String>();
		
		for(String unit : arr)
		{
			_unit_set.add(unit);
		}
		
        init();
	}
	
	private Segmentor(InputStream is) throws IOException
	{
		String [] arr = SUFFIX.split("\\|");
		_suffix_set=new HashSet<String>();
		
		for(String unit : arr)
		{
			_suffix_set.add(unit);
		}
		
		arr = UNIT.split("\\|");
		_unit_set=new HashSet<String>();
		
		for(String unit : arr)
		{
			_unit_set.add(unit);
		}
		
        init(is);
	}

	private Segmentor( Map<String,String> dict ) throws FileNotFoundException {
		String [] arr = SUFFIX.split("\\|");
		_suffix_set=new HashSet<String>();
		
		for(String unit : arr)
		{
			_suffix_set.add(unit);
		}
		
		arr = UNIT.split("\\|");
		_unit_set=new HashSet<String>();
		
		for(String unit : arr)
		{
			_unit_set.add(unit);
		}
		
        init( dict );
	}
	
	private Segmentor( List<String> dict ) throws FileNotFoundException {
		String [] arr = SUFFIX.split("\\|");
		_suffix_set=new HashSet<String>();
		
		for(String unit : arr)
		{
			_suffix_set.add(unit);
		}
		
		arr = UNIT.split("\\|");
		_unit_set=new HashSet<String>();
		
		for(String unit : arr)
		{
			_unit_set.add(unit);
		}
		
        init( dict );
	}
	
	public static Segmentor getInstance()
	{
        if (_singleton== null)
        {
        	synchronized (Segmentor.class)
        	{
        		if (_singleton== null)
        		{
        			try
        			{
        				_singleton= new Segmentor();
        			}
        			catch( Exception e )
        			{
        				System.err.println("gkm: new Segmentor error!");
        			}
       			}
            }
        }
        return _singleton;
	}
	
	/**
	 * gkm: get an instance of segmentor with customer dict.
	 */
	public static Segmentor getInstance(InputStream customer_dict)
	{
        if (_singleton== null)
        {
        	synchronized (Segmentor.class)
        	{
        		if (_singleton== null)
        		{
        			try
        			{
        				_singleton= new Segmentor(customer_dict);
        			}
        			catch( Exception e )
        			{
        				System.err.println("gkm: new Segmentor error!");
        			}
       			}
            }
        }
        return _singleton;
	}
	
	/**
	 * gkm: get an instance of segmentor with customer dict.
	 */
	public static Segmentor getInstance(List<String> customer_dict)
	{
        if (_singleton== null)
        {
        	synchronized (Segmentor.class)
        	{
        		if (_singleton== null)
        		{
        			try
        			{
        				_singleton= new Segmentor(customer_dict);
        			}
        			catch( Exception e )
        			{
        				System.err.println("gkm: new Segmentor error!");
        			}
       			}
            }
        }
        return _singleton;
	}	
	
	// SJP each token is split into characters in a Trie tree and assigned right and left entropy
	// the right and left entropy is weighted by token's length and a fixed weight
	private void init()
	{
		try
		{
			Properties prop = new Properties();
			prop.load(new FileInputStream(new File(path+"/conf/entropy.properties")));
			String infors = prop.getProperty("infor");
//			String add = prop.getProperty("add");
			loadModel(path + infors);    ///dict_high.model
//	        addDict(path + add, 1, 1000, "A");
//	        addDict("/set.dict", 2, 10.0, "S");
//	        addDict("/general.dict", 2, 1.0, TAG);   //18.0
//	        addDict("/qualifier_high.dict", 2, 2.0, "Q" );  //24.0
//	        addDict("/brand_high.dict", 2, 26.0, "B" );
//	        addDict("/product_manual_check_gkm.dict", 2, 25.0, "P" );
		}
		catch( Exception e)
		{
			System.err.println("gkm: segmentor initializing error!");
			e.printStackTrace();
		}
	}
		
	private void init( InputStream is )
	{
		try
		{
			Properties prop = new Properties();
			prop.load(new FileInputStream(new File(path+"/conf/entropy.properties")));
			String infors = prop.getProperty("infor");
//			String add = prop.getProperty("add");
//			addDict(path + add, 1, 1000, "A");
			loadModel(path + infors);

//	        addDict("/attachment.dict", 1, 25.0, "A");
//	        addDict("/set.dict", 2, 10.0, "S");
//	        addDict("/general.dict", 2, 1.0, TAG);
//	        addDict("/qualifier_high.dict", 2, 2.0, "Q" );
//	        addDict("/brand_high.dict", 2, 26.0, "B" );
//	        addDict("/product_manual_check_gkm.dict", 2, 25.0, "P" );
		}
		catch( Exception e)
		{
			System.err.println("gkm: segmentor initializing error!");
			e.printStackTrace();
		}
		
		try
		{
			addCustomerDict(is, 1, 100.0);
		}
		catch( Exception e)
		{
			System.err.println("gkm: customer dictionary error!");
			e.printStackTrace();
		}
	}
	
	private void init( Map<String,String> dict )
	{
		try
		{
			Properties prop = new Properties();
			prop.load(new FileInputStream(new File(path+"/conf/entropy.properties")));
			String infors = prop.getProperty("infor");
//			String add = prop.getProperty("add");
//			addDict(path + add, 1, 1000, "A");
			loadModel(path + infors);

//	        addDict("/attachment.dict", 1, 25.0, "A");
//	        addDict("/set.dict", 2, 10.0, "S");
//	        addDict("/general.dict", 2, 1.0, TAG);
//	        addDict("/qualifier_high.dict", 2, 2.0, "Q" );
//	        addDict("/brand_high.dict", 2, 26.0, "B" );
//	        addDict("/product_manual_check_gkm.dict", 2, 25.0, "P" );
		}
		catch( Exception e)
		{
			System.err.println("gkm: segmentor initializing error!");
			e.printStackTrace();
		}
		
		try
		{
			addCustomerDict( dict, 1, 100.0);
		}
		catch( Exception e)
		{
			System.err.println("gkm: customer dictionary error!");
			e.printStackTrace();
		}
	}
		
	private void init( List<String> dict )
	{
		try
		{
			// SJP load right and left entropy to build a Trie tree with tag 'O' (others)
			Properties prop = new Properties();
			prop.load(new FileInputStream(new File(path+"/conf/entropy.properties")));
			String infors = prop.getProperty("infor");
//			String add = prop.getProperty("add");
//			addDict(path + add, 1, 1000, "A");
			loadModel(path +infors);
			
//	        addDict("/attachment.dict", 1, 25.0, "A");
//	        addDict("/set.dict", 2, 10.0, "S");
//	        addDict("/general.dict", 2, 1.0, TAG);
//	        addDict("/qualifier_high.dict", 2, 2.0, "Q" );
//	        addDict("/brand_high.dict", 2, 26.0, "B" );
//	        addDict("/product_manual_check_gkm.dict", 2, 25.0, "P" );
		}
		catch( Exception e)
		{
			System.err.println("gkm: segmentor initializing error!");
			e.printStackTrace();
		}
		
		try
		{
			addCustomerDict( dict, 1, 100.0);
		}
		catch( Exception e)
		{
			System.err.println("gkm: customer dictionary error!");
			e.printStackTrace();
		}
	}	
	
	public void reinitialize( InputStream is )
	{
		init( is );
	}

	public void reinitialize( Map<String,String> dict )
	{
		init( dict );
	}
	
	public void reinitialize( List<String> dict )
	{
		init( dict );
	}
	
	// SJP import a word dict with left entropy and right entropy to build a Trie tree
	private void loadModel(String model) throws IOException
	{		
		_trie = new TrieNode();
		int m =0;

		//new InputStreamReader( this.getClass().getResourceAsStream(model), "utf8")
		BufferedReader br = new BufferedReader( new FileReader(model));

		for ( String line=br.readLine(); line!= null; line=br.readLine())
		{
//			line = line.trim().toLowerCase();  //去掉trim()，不然^类型的term都去掉了
			line = line.toLowerCase();
			
			if(line.isEmpty())
				continue;			
			
			String[] arr = line.split("\t");
			m+=1;
			
			if(arr.length<3)
			{
				System.out.println(line);
				System.out.println(m);
				continue;
			}
			
			String word = arr[0];
//			if(word.equals("版"))
//			{
//				System.out.println(m);
//			}
						
			double left_entropy= 0;
			double right_entropy=0;

			try
			{
				left_entropy = Double.parseDouble(arr[1]);
				right_entropy = Double.parseDouble(arr[2]);
			}
			catch(Exception e)
			{
				continue;
			}
			
			TrieNode parent = _trie;
			TrieNode node = _trie;
			
			double word_len = word.length();

			for (int i = 0; i < word_len; i++)
			{
				Character ch = word.charAt(i);
				node = parent.getChild(ch);

				if (node == null)
				{
					node = new TrieNode();
					node.setCharacter(ch);
					parent.addChild(node);
				}
				parent = node;
			}

			node.setWord(word);
			node.setTag(TAG);

			if(word_len==1)
			{
				word_len=0.2;   //0.4
				node.setLeftEntropy( (left_entropy+1.0)*word_len);
				node.setRightEntropy( (right_entropy+1.0)*word_len );
			}
			else if (word_len<=3)
			{
				word_len*=(1.0+0.5*word_len);
				node.setLeftEntropy( (left_entropy+9.0  )*word_len);   //9.0
				node.setRightEntropy( (right_entropy+9.0)*word_len );
			}
			else
			{
				word_len*=(1.0+0.5*word_len);
				node.setLeftEntropy( (left_entropy+8.0)*word_len);   //8.0
				node.setRightEntropy( (right_entropy+8.0)*word_len );
			}
			
			node.setLeftEntropy( left_entropy);
			node.setRightEntropy( right_entropy);
		}
		br.close();
	}

	private void addDict( String model, int len,  double weight, String tag) throws IOException
	{
//		BufferedReader br = new BufferedReader( new InputStreamReader( is, "utf8" ) );
		BufferedReader br = new BufferedReader( new FileReader(model));

		for ( String line=br.readLine(); line!= null; line=br.readLine())
		{
			line = line.trim().toLowerCase();
			
			if(line.isEmpty())
				continue;			
			
			String word = line.split("\t")[0];
			
			double word_len = word.length();
			
			if( word_len<len )
				continue;
			
			TrieNode parent = _trie;
			TrieNode node = _trie;
						
			for (int i = 0; i < word_len; i++)
			{
				Character ch = word.charAt(i);
				node = parent.getChild(ch);
				
				if (node == null)
				{
					node = new TrieNode();
					node.setCharacter(ch);
					parent.addChild(node);
				}
				parent = node;
			}
			
			if(word_len==1)
			{
				word_len*=0.4;
			}
			else
			{
				word_len*=(0.6+0.1*word_len);
			}
			
			double score = weight*word_len;
			
			if( node.getWord()==null )
			{
				node.setLeftEntropy(score);
				node.setRightEntropy(score);
				node.setWord(word);
				node.setTag(tag);
			}
			else
			{
				node.setAdditionalScore(score);
				
				if( node.getTag().equals(TAG) )
				{
					node.setTag(tag);
				}
				else
				{
					if( !node.getTag().contains(tag) && !tag.equals(TAG))
					{
						node.setTag(node.getTag()+tag);
					}
				}
			}
		}
		br.close();
	}

//	private void addDict( String file, int len,  double weight, String tag) throws IOException
//	{
//		InputStream is =  this.getClass().getResourceAsStream(file);
//
//		addDict( is, len, weight, tag);
//	}
		
	private void addCustomerDict( InputStream is,  int len, double weight ) throws IOException
	{
		BufferedReader br = new BufferedReader( new InputStreamReader( is, "utf8" ) );

		for ( String line=br.readLine(); line!= null; line=br.readLine())
		{
			line = line.trim();
			
			if(line.isEmpty())
				continue;			
			
			String [] arr = line.split("\t"); 
			
			if( arr.length!=2 )
			{
				System.err.println("customer dict format error: "+line);
				continue;
			}
			
			String word = arr[0].toLowerCase(); 
			
			String tag = arr[1];
			
			double word_len = word.length();
			
			if( word_len<len )
				continue;
			
			TrieNode parent = _trie;
			TrieNode node = _trie;
						
			for (int i = 0; i < word_len; i++)
			{
				Character ch = word.charAt(i);
				node = parent.getChild(ch);
				
				if (node == null)
				{
					node = new TrieNode();
					node.setCharacter(ch);
					parent.addChild(node);
				}
				parent = node;
			}
			
			if(word_len==1)
			{
				word_len*=0.4;
			}
			else
			{
				word_len*=(0.6+0.1*word_len);
			}
			
			double score = weight*word_len;
			
			if( node.getWord()==null )
			{
				node.setLeftEntropy(score);
				node.setRightEntropy(score);
				node.setWord(word);
				node.setTag(tag);
			}
			else
			{
				node.setAdditionalScore(score);
				
				if( node.getTag().equals(TAG) )
				{
					node.setTag(tag);
				}
				else
				{
					if( !node.getTag().contains(tag) && !tag.equals(TAG))
					{
						node.setTag(node.getTag()+tag);
					}
				}
			}
		}
		br.close();
	}
		
	private void addCustomerDict( Map<String,String> dict,  int len, double weight ) throws IOException
	{
		for ( Entry<String, String> entry : dict.entrySet() )
		{
			String word = MyCharacter.semiangle( entry.getKey().toLowerCase() ); 
			
			String tag = entry.getValue();
			
			double word_len = word.length();
			
			if( word_len<len )
				continue;
			
			TrieNode parent = _trie;
			TrieNode node = _trie;
						
			for (int i = 0; i < word_len; i++)
			{
				Character ch = word.charAt(i);
				node = parent.getChild(ch);
				
				if (node == null)
				{
					node = new TrieNode();
					node.setCharacter(ch);
					parent.addChild(node);
				}
				parent = node;
			}
			
			if(word_len==1)
			{
				word_len*=0.4;
			}
			else
			{
				word_len*=(0.6+0.1*word_len);
			}
			
			double score = weight*word_len;
			
			if( node.getWord()==null )
			{
				node.setLeftEntropy(score);
				node.setRightEntropy(score);
				node.setWord(word);
				node.setTag(tag);
			}
			else
			{
				node.setAdditionalScore(score);
				
				if( node.getTag().equals(TAG) )
				{
					node.setTag(tag);
				}
				else
				{
					if( !node.getTag().contains(tag) && !tag.equals(TAG))
					{
						node.setTag(node.getTag()+tag);
					}
				}
			}
		}
	}
	
	private void addCustomerDict( List<String> dict,  int len, double weight ) throws IOException
	{
		for ( String word : dict )
		{
			word = MyCharacter.semiangle( word.toLowerCase() ); 
			
			String tag = "P";
			
			double word_len = word.length();
			
			if( word_len<len )
				continue;
			
			TrieNode parent = _trie;
			TrieNode node = _trie;
						
			for (int i = 0; i < word_len; i++)
			{
				Character ch = word.charAt(i);
				node = parent.getChild(ch);
				
				if (node == null)
				{
					node = new TrieNode();
					node.setCharacter(ch);
					parent.addChild(node);
				}
				parent = node;
			}
			
			if(word_len==1)
			{
				word_len*=0.4;
			}
			else
			{
				word_len*=(0.6+0.1*word_len);
			}
			
			double score = weight*word_len;
			
			if( node.getWord()==null )
			{
				node.setLeftEntropy(score);
				node.setRightEntropy(score);
				node.setWord(word);
				node.setTag(tag);
			}
			else
			{
				node.setAdditionalScore(score);
				
				if( node.getTag().equals(TAG) )
				{
					node.setTag(tag);
				}
				else
				{
					if( !node.getTag().contains(tag) && !tag.equals(TAG))
					{
						node.setTag(node.getTag()+tag);
					}
				}
			}
		}
	}
		
	private List<TrieNode> commonPrefixSearch(String text)
	{
		if (_trie == null)
		{
			System.err.println("Segmentor is not initialized !");
			return null;
		}
		
		List<TrieNode> list = new ArrayList<TrieNode>();
		
		TrieNode node = _trie;
		
		for (int i = 0; i < text.length(); i++)
		{
			Character ch = text.charAt(i);
			node = node.getChild(ch);
			
			if (node == null)
			{
				break;
			}
			else
			{
				if( node.getWord()!=null )
					list.add(node);
			}
		}
		
		return list;
	}
	
	private TrieNode search(String word)
	{
		if (_trie == null)
		{
			System.err.println("Segmentor is not initialized !");
			return null;
		}

		TrieNode node = _trie;
		
		for (int i = 0; i < word.length(); i++)
		{
			Character ch = word.charAt(i);
			node = node.getChild(ch);
			
			if (node == null)
			{
				break;
			}
		}
		return node;
	}
	
	// SJP use Trie tree to initialize the segment
	// What is preSegment supposed to do?
	private List<Segment> preSegment(String line)
	{
		line = line.toLowerCase();
		
		List<Segment> segs = new ArrayList<Segment>();
		
		Segment start = new Segment();
		start.word = Segment.START_WORD;
		start.tag=TAG;
		start.pos = Segment.START_POS;
		start.left_score = CONST;
		start.right_score = CONST;
		start.pre = null;
		segs.add(start);
	
		for (int i = 0; i < line.length(); i++)
		{
			String text = line.substring(i);
			
			List<TrieNode> list = commonPrefixSearch(text);

			boolean b=true;
			
			for( TrieNode node : list )
			{
				b=false;
				
				Segment seg = new Segment();
				seg.word = node.getWord();
				seg.tag = node.getTag();
				seg.pos = i+seg.word.length()-1;
				
				if (i == 0)
				{
					seg.pre = Segment.START_POS;				
				}
				else
				{
					seg.pre = i-1;
				}
				
				seg.left_score = node.getLeftScore();
				seg.right_score = node.getRightScore();			
				segs.add(seg);
			}
			
			if(b)
			{
				Segment seg = new Segment();
				seg.word = text.substring(0,1);
				seg.tag=TAG;
				seg.pos = i;
				
				if (i == 0)
				{
					seg.pre = Segment.START_POS;				
				}
				else
				{
					seg.pre = i-1;
				}
				
				seg.left_score = CONST;
				seg.right_score = CONST;
				segs.add(seg);
			}
		}

//		for (Segment seg : segs) {
//			System.out.println(seg.word + "\t" + seg.left_score  + "\t" + seg.right_score);
//		}
		
		Segment end = new Segment();
		end.word = Segment.END_WORD;
		end.tag=TAG;
		end.pos = Segment.END_POS;
		end.pre = line.length()-1;
		end.left_score = CONST;
		end.right_score = CONST;
		segs.add(end);
				
		return segs;
	}
	
	private ArrayList<List<MyPair<String,String>> >findOptimalPath(List<Segment> segs)
	{		
		if (segs == null || segs.size() < 3)
		{
			return null;
		}
			
		int size = segs.size();

		Graph graph = new Graph(size);
		
		for (int i = 0; i < size; i++)
		{
			Segment left = segs.get(i);

			Integer pos = left.pos;
			
			for (int j = 0; j < size; j++)
			{
				Segment right = segs.get(j);

				if ( right.pre!=null && right.pre.equals(pos) )
				{
					double score = left.right_score+right.left_score;
					graph.addEdge(i, j, score);
//					System.out.println(i + "\t" + j + "\t" + left.word + "\t" + right.word + "\t" + score);
				}
			}
		}

		List<Integer> path = graph.findLongestPath();
		
		size = path.size();
		
		List<MyPair<String,String>> result = new ArrayList<MyPair<String,String>>();
		List<MyPair<String,String>> result_score = new ArrayList<MyPair<String,String>>();
		ArrayList<List<MyPair<String,String>> > all = new  ArrayList<List<MyPair<String,String>>>();
		
		for (Integer i : path)
		{
			result.add(new MyPair<String,String>(segs.get(i).word, segs.get(i).tag) );
			result_score.add(new MyPair<String,String>(segs.get(i).left_score.toString(), segs.get(i).right_score.toString()));
		}
		all.add(result);
		all.add(result_score);
		return all;
	}
	
	private List<MyPair<String,String>> combine( List<MyPair<String,String>> segs )
	{
		List<MyPair<String,String>> list = new ArrayList<MyPair<String,String>>();		

		MyPair<String,String> left_pair = null;		
		MyPair<String,String> right_pair = null;
		
		for ( MyPair<String,String> seg : segs )
		{
			right_pair = seg;
			
			if( left_pair==null )
			{
				left_pair = right_pair;
				continue;
			}

			char left_char = left_pair.key.charAt(left_pair.key.length()-1);
			char right_char = right_pair.key.charAt(0);
			
			if( MyLetter.bothDigit(left_char, right_char) || MyLetter.bothLetter(left_char, right_char) )
			{												
				int pos=0;
				int len = right_pair.key.length();
				for( pos=0; pos<len; pos++ )
				{
					char ch = right_pair.key.charAt(pos);
					if( !MyLetter.isDigit(ch) && !MyLetter.isLetter(ch) )
					{
						break;
					}
				}
				
				left_pair.key+=right_pair.key.substring(0,pos);
				
				TrieNode node = search(left_pair.key);
				
				if( node!=null && node.getTag()!=null ) 
				{
					left_pair.value=node.getTag();										
				}
				else
				{
					left_pair.value=TAG;	
				}

				if( pos!=right_pair.key.length() )
				{
					list.add(left_pair);
					
					right_pair.key=right_pair.key.substring(pos);

					node = search(right_pair.key);
					
					if(  node!=null && node.getTag()!=null ) 
					{
						right_pair.value=node.getTag();										
					}
					else
					{
						right_pair.value=TAG;	
					}
					
					left_pair = right_pair;
				}
			}
			else
			{
				list.add(left_pair);
				left_pair = right_pair;
			}
		}
		
		list.add(left_pair);
		
		return list;
	}
	
	public ArrayList<List<MyPair<String, String>>> tag(String line )
	{
		ArrayList<List<MyPair<String,String>> > result = new ArrayList<List<MyPair<String,String>> >();
		
		if( line==null || line.isEmpty() )
			return result;
		
		// SJP preSegment: extract segments of a string and get the initialize right and left entropy values
		ArrayList<List<MyPair<String,String>> > segs = findOptimalPath(preSegment(line));
		List<MyPair<String,String>> segs_one = new ArrayList<MyPair<String,String>>();
		List<MyPair<String,String>> result_one = new ArrayList<MyPair<String,String>>();
		segs_one = segs.get(0);
		
//		segs = combine(segs);
//
		int size = segs_one.size()-1;

		int i;
//
		for( i=0; i<size; i++ )
		{
//			if( segs.get(i).value.contains("B") )
//			{
//				result.add( segs.get(i) );
//				continue;
//			}
//
			String left = segs_one.get(i).key;
			String right = segs_one.get(i+1).key;

			if( MyLetter.allDigit(left) && _unit_set.contains(right) )
			{
				String word = left+right;

				String tag = TAG;

				TrieNode node = search(word);

				if( node!=null && node.getTag()!=null )
				{
					tag = node.getTag();
				}

				result_one.add(new MyPair<String,String>(word, tag));

				i++;
			}

			else
			{
//				if( _suffix_set.contains(right) )
//				{
//					String word = left+right;
//					String tag = TAG;
//
//					TrieNode node = search(word);
//					if( node!=null && node.getTag()!=null )
//					{
//						tag = node.getTag();
//					}
//
//					result.add(new MyPair<String,String>(word, tag));
//					i++;
//				}
//				else
//				{
					result_one.add( segs_one.get(i) );
//				}
			}
		}
//
		if( i==size )
		{
			result_one.add(segs_one.get(i));
		}
		result.add(result_one);
		result.add(segs.get(1));
		
		return result;
	}
	
	public List<String> seg(String line)
	{		
		List<String> result = new ArrayList<String>();
		
		if( line==null || line.isEmpty())
			return result;

		ArrayList<List<MyPair<String,String>> > list = tag(line);
		
		for (MyPair<String,String> pair : list.get(0) )
		{
			result.add(pair.key);
		}
		
		return result;
	}
	
	private static class Segment
	{
		public final static String START_WORD = "START";
		public final static String END_WORD = "END";
		
		public final static Integer START_POS = -1;
		public final static Integer END_POS = 1000000;
		
		public String word=null;
		public String tag=null;
		
		public Integer pos=null;
		public Integer pre=null;
		
		public Double left_score=null;
		public Double right_score=null;
	}
		
	private static class Graph
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

        	dynamicSearch();
           
            List<Integer> path = new ArrayList<Integer>();
            
            int pre = _longest_path_predecessor[_vex_num-1];
            
            if(pre==-1)
            	return path;

            while (pre!= 0)
            {
            	path.add(pre);
            	pre = _longest_path_predecessor[pre];
            }

            Collections.reverse(path);
            
            return path;
        }
  
        private void dynamicSearch()
        {        	
        	_max_distances[0] = 0;
        	
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

	private static class MyStack
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
}
