using UnityEngine;
using System;
using System.Collections;
using System.Collections.Generic;

public class UnityThread : MonoBehaviour {

    private static UnityThread mCurrent;

    public static UnityThread Current
    {
        get
        {
            return mCurrent;
        }
    }

    List<Action> actionList = new List<Action>();

    #region unity

    void Awake()
    {
        mCurrent = this;
    }


	
	// Update is called once per frame
	void Update () {
        lock (actionList)
        {
            for (int i = 0; i < actionList.Count; ++i)
            {
                try
                {
                    Action action = actionList[i];
                    if(action != null)
                    {
                        action.Invoke();
                    }
                }
                catch (Exception e)
                {
                    Debug.LogError("UnityThread exception e:" + e.ToString());
                }
               
            }

            actionList.Clear();
        }
    }

    #endregion

    public void Queue(Action action)
    {
        lock (actionList)
        {
            actionList.Add(action);
        }

    }


}
