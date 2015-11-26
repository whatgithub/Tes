/*
 * 文件名：T1.java 版权：Copyright by www..com 描述： 修改人：symbol 修改时间：2015年11月19日 跟踪单号： 修改单号： 修改内容：
 */

/**
 * 〈一句话功能简述〉<br/>
 * 〈功能详细描述〉
 * 
 * @author symbol
 * @version 2015年11月19日
 * @see T1
 * @since
 */
public class T1 extends T4
{
    /**
     * 
     */
    private String name;

    /**
     * Description:这里用一句话描述这个方法的作用.<br/>
     * TODO(这里描述这个方法适用条件 – 可选).<br/>
     * TODO(这里描述这个方法的执行流程 – 可选).<br/>
     * TODO(这里描述这个方法的使用方法 – 可选).<br/>
     * TODO(这里描述这个方法的注意事项 – 可选).<br/>
     * 
     * @param args
     *            args
     * @see
     */
    public static void main(String[] args)
    {
        double d = 1.2;
        int i = (int)d;
        d = (int)d * i;
        System.out.println(i);
        int key = 1;
        switch (key)
        {
            case 1:
                System.out.println(1);
                break;
            case 2:
                System.out.println(2);
                break;
            default:
                System.out.println(0);
                break;
        }
        if ((d = i) > 0)
        {
            d = i;
        }
        T1 t = new T1();
        t.addf();

    }

    /**
     * Description:这里用一句话描述这个方法的作用.<br/>
     * TODO(这里描述这个方法适用条件 – 可选).<br/>
     * TODO(这里描述这个方法的执行流程 – 可选).<br/>
     * TODO(这里描述这个方法的使用方法 – 可选).<br/>
     * TODO(这里描述这个方法的注意事项 – 可选).<br/>
     * 
     * @return qqq saw
     * @see
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Description:这里用一句话描述这个方法的作用.<br/>
     * TODO(这里描述这个方法适用条件 – 可选).<br/>
     * TODO(这里描述这个方法的执行流程 – 可选).<br/>
     * TODO(这里描述这个方法的使用方法 – 可选).<br/>
     * TODO(这里描述这个方法的注意事项 – 可选).<br/>
     * 
     * @see
     */
    public static void addf()
    {

    }
}
